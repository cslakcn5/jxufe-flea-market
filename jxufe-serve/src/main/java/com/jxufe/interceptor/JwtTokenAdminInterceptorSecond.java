package com.jxufe.interceptor;

import cn.hutool.core.util.ObjUtil;
import com.jxufe.constant.ExceptionConstant;
import com.jxufe.context.BaseContext;
import com.jxufe.properties.JwtProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 进行拦截
 */
@Component
@Slf4j
public class JwtTokenAdminInterceptorSecond implements HandlerInterceptor {

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handle) {

        if( ObjUtil.isEmpty(BaseContext.getCurrentId()) ){
            response.setStatus(401);
            throw new RuntimeException( ExceptionConstant.LOGIN_EXCEPTION );
        }
       return true;
    }

}

