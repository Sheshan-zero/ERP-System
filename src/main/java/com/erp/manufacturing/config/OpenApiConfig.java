package com.erp.manufacturing.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI manufacturingErpOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Manufacturing ERP API")
                        .version("1.0.0")
                        .description("REST API documentation for the Manufacturing ERP backend.")
                        .contact(new Contact()
                                .name("Manufacturing ERP Team")
                                .email("support@manufacturing-erp.local")));
    }
}
