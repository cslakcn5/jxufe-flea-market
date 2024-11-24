package com.jxufe.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

import java.util.ArrayList;
import java.util.List;

/**
 * Swagger2配置信息
 * 这里分了两组显示
 * 第一组是api，当作用户端接口
 * 第二组是admin，当作后台管理接口
 * 也可以根据实际情况来减少或者增加组
 *
 * @author Eric
 * @date 2023-07-30 22:17
 */

@Configuration
@EnableSwagger2WebMvc
public class Knife4jConfig {

    private ApiInfo adminApiInfo() {
        return new ApiInfoBuilder()
                .title("黑马点评")
                .description("黑马点评接口文档")
                .version("1.0")
                .contact(new Contact("逍遥", "https://www.jxufe.top", "1420283526@qq.com"))
                .build();
    }

    /**
     * 第一组：api
     * @return
     */
    @Bean
    public Docket webApiConfig() {

        Docket webApi = new Docket(DocumentationType.SWAGGER_2)
                .groupName("用户端接口")
                .apiInfo(adminApiInfo())
                .groupName("1.x版本")
                .select()
                //只显示api路径下的页面
                .apis(RequestHandlerSelectors.basePackage("com.jxufe"))
                .paths(PathSelectors.any())
                .build();

        return webApi;
    }


}

