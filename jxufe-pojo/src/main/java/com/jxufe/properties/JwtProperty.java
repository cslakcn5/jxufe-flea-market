package com.jxufe.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "jxufe.jwt")
@NoArgsConstructor
@AllArgsConstructor
public class JwtProperty {

    /*
    * 客户端生成jwt相关配置
    * */
    private String secretKey;

    private long ttl;

    private String tokenName;

}
