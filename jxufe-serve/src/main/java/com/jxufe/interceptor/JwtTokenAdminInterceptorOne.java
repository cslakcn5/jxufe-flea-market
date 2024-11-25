package com.jxufe.interceptor;


import cn.hutool.core.util.StrUtil;
import com.jxufe.constant.ExceptionConstant;
import com.jxufe.constant.SystemConstants;
import com.jxufe.context.BaseContext;
import com.jxufe.properties.JwtProperty;
import com.jxufe.constant.RedisConstants;
import com.jxufe.utils.JwtUtil;
import com.jxufe.utils.StringUtil;
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
 * 只负责刷新token保存时间
 */
@Component
@Slf4j
public class JwtTokenAdminInterceptorOne implements HandlerInterceptor {

    @Autowired
    private JwtProperty jwtProperty;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    //没有token则正常返回，有token则刷新时间
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handle) {
        // 1.获取token, 没有token则是未登录
        String jwt = request.getHeader(jwtProperty.getTokenName());
        String token;

        if ( StrUtil.isBlank(jwt) ) {
            return true;
        }
        else {
            token =  StringUtil.subString(jwt, SystemConstants.TOKEN_NUMBER);
        }
        //2.判断redis中是否含有该token的值，若有则刷新时间，没有则放行  没有则说明该token已过期
        String entries = redisTemplate.opsForHash().entries(RedisConstants.LOGIN_USER_KEY + token).toString();
        if( Optional.ofNullable(entries).isEmpty() || cn.hutool.core.util.StrUtil.equals(entries, "{}") ){
            return true;
        }
        redisTemplate.expire(RedisConstants.LOGIN_USER_KEY + StringUtil.subString(jwt, SystemConstants.TOKEN_NUMBER), 30, TimeUnit.MINUTES);

        try {
            String userId = JwtUtil.parseJWT(jwtProperty.getSecretKey(), jwt).get("id").toString();
            BaseContext.setCurrentId(Long.parseLong(userId));

        } catch (RuntimeException e) {
            response.setStatus(401);
            throw new RuntimeException(ExceptionConstant.LOGIN_EXCEPTION);
        }
        return true;
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        BaseContext.removeCurrentId();
    }

}

