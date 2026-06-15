package com.erp.manufacturing.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenApiConfig {

    @Bean
    public OpenAPI manufacturingErpOpenApi() {

        return new OpenAPI()

                .addSecurityItem(
                        new SecurityRequirement().addList("bearerAuth")
                )

                .components(
                        new io.swagger.v3.oas.models.Components()
                                .addSecuritySchemes(
                                        "bearerAuth",
                                        new io.swagger.v3.oas.models.security.SecurityScheme()
                                                .name("bearerAuth")
                                                .type(io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                                .in(In.HEADER)
                                )
                )

                .info(
                        new Info()
                                .title("Manufacturing ERP API")
                                .version("1.0.0")
                                .description("REST API documentation for the Manufacturing ERP backend.")
                                .contact(
                                        new Contact()
                                                .name("Manufacturing ERP Team")
                                                .email("support@manufacturing-erp.local")
                                )
                );
    }
}