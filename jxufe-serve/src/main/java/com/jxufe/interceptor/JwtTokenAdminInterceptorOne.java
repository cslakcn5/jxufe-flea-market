package com.jxufe.interceptor;

import cn.hutool.core.util.StrUtil;
import com.jxufe.context.BaseContext;
import com.jxufe.properties.JwtProperty;
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
public class JwtTokenAdminInterceptorOne implements HandlerInterceptor {

    @Autowired
    private JwtProperty jwtProperty;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handle) {
        //1.获取token
        String jwt = request.getHeader(jwtProperty.getTokenName());
        String token;
        if ( StrUtil.isBlank(jwt) ) {
            return true;
        }
        else {
            token = jwt.substring(0, 6);
        }
        //2.判断redis中是否含有该token的值，若有则刷新时间，没有则放行
        String entries = redisTemplate.opsForHash().entries(RedisConstants.LOGIN_USER_KEY + token.substring(0, 6)).toString();
        if( Optional.ofNullable(entries).isEmpty() ){
            return true;
        }
        redisTemplate.expire(RedisConstants.LOGIN_USER_KEY + token.substring(0, 6), 30, TimeUnit.MINUTES);
        return true;
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {

        BaseContext.removeCurrentId();
    }

}

