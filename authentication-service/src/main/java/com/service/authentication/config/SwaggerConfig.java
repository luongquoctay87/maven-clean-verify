package com.service.authentication.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Value("${api.info.title: api.info.title}")
    private String title;

    @Value("${api.info.description: api.info.description}")
    private String description;

    @Value("${api.info.version: api.info.version}")
    private String version;

    @Value("${api.info.term-of-service: api.info.terms-of-service}")
    private String termOfService;

    @Value("${api.info.contact.name: api.info.contact.name}")
    private String contactName;

    @Value("${api.info.contact.email: api.info.contact.email}")
    private String contactEmail;

    @Value("${api.info.contact.url: api.info.contact.url}")
    private String contactUrl;

    @Value("${api.info.license.name: api.info.license.name}")
    private String licenseName;

    @Value("${api.info.license.url: api.info.license.url}")
    private String licenseUrl;

    @Bean
    public GroupedOpenApi groupedOpenApi(){
        return GroupedOpenApi.builder()
                .group("Authentication Management")
                .packagesToScan("com.service.authentication.controller")
                .pathsToMatch("/**")
                .build();
    }

    @Bean
    public OpenAPI api() {
        return new OpenAPI().info(info());
    }

    private Info info() {
        return new Info()
                .title(title)
                .description(description)
                .version(version)
                .contact(new Contact().name(contactName).email(contactEmail).url(contactUrl))
                .license(new License().name(licenseName).url(licenseUrl));
    }
}
