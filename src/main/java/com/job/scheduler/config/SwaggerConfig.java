package com.job.scheduler.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.job.scheduler"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(addMetaData());
    }

    private ApiInfo addMetaData() {
        return new ApiInfo("Job Scheduler Documentation",
                "", "1.0", "",
                new Contact("Madhav-Jangala", "https://www.linkedin.com/in/madhav-jangala-14850418/", "jvmadhav1987@gmail.com"),
                "", "",
                Collections.emptyList());
    }
}
