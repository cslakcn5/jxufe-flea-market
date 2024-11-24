package com.jxufe.config;

import com.jxufe.interceptor.JwtTokenAdminInterceptorOne;
import com.jxufe.interceptor.JwtTokenAdminInterceptorSecond;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtTokenAdminInterceptorSecond jwtTokenAdminInterceptorSecond;

    @Autowired
    private JwtTokenAdminInterceptorOne jwtTokenAdminInterceptorOne;

    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(jwtTokenAdminInterceptorOne).addPathPatterns("/**")
                .excludePathPatterns("/doc.html/**")
                .order(0);

        registry.addInterceptor(jwtTokenAdminInterceptorSecond).addPathPatterns("/**")
                .excludePathPatterns("/user/code/**")
                .excludePathPatterns("/user/login/**")
                .excludePathPatterns("/user/logout/**")
                .excludePathPatterns("/blog/hot/**")
                .excludePathPatterns("/shop-type/list")
                .excludePathPatterns("/doc.html/**")
                .excludePathPatterns("/user/me").order(1);

    }

    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }
}
