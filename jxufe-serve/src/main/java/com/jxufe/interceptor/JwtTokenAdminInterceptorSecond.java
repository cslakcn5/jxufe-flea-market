package com.jxufe.interceptor;

import com.jxufe.context.BaseContext;
import com.jxufe.properties.JwtProperty;
import com.jxufe.utils.JwtUtil;
import com.jxufe.utils.RedisConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * jwt令牌校验的拦截器
 */
@Component
@Slf4j
public class JwtTokenAdminInterceptorSecond implements HandlerInterceptor {

    @Autowired
    private JwtProperty jwtProperty;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handle) {
        //1.获取token
        String token = request.getHeader(jwtProperty.getTokenName());
        //2. 检验token
        try {
            JwtUtil.parseJWT(jwtProperty.getSecretKey(), token);
        } catch (RuntimeException e) {
            log.error("token解析错误", e);
            return false;
        }
        //3.检验通过后读取redis中的用户信息
        String userId;
        try {
            userId = redisTemplate.opsForHash().entries(RedisConstants.LOGIN_USER_KEY + token.substring(0,6)).get("id").toString();
        }
        catch (Exception e){
            log.error("请重新登录", e);
            response.setStatus(401);
            throw new RuntimeException("请重新登录");
        }

        //4.通过token获取保存在redis里的用户信息

        if( Optional.ofNullable(userId).isEmpty() ){
            return false;
        }
        //5.将用户信息储存在线程中
        long id = Long.parseLong(userId);
        BaseContext.setCurrentId(id);
        log.info("当前登录的用户id为:{}", id);
        //6.格式化token在redis中存在的时间
        redisTemplate.expire(RedisConstants.LOGIN_USER_KEY + token, 30, TimeUnit.MINUTES);
        return true;
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {

        BaseContext.removeCurrentId();
    }

}

