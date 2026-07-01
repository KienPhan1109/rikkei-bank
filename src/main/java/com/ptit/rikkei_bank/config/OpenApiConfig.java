package com.ptit.rikkei_bank.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "Bearer Authentication";

        return new OpenAPI()
                .info(new Info()
                        .title("Rikkei Bank RESTful API")
                        .description("Tài liệu API tương tác cho Hệ thống Quản lý Ngân hàng Rikkei Bank. "
                                + "Hỗ trợ các nghiệp vụ: Xác thực JWT, Định danh eKYC, Quản lý Tài khoản, "
                                + "Chuyển khoản, Nạp/Rút tiền mặt và Giám sát hệ thống.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Phan Trung Kiên")
                                .email("kien.phan@ptit.edu.vn"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Nhập Access Token (JWT) vào đây để xác thực. "
                                                + "Ví dụ: eyJhbGciOiJIUzI1NiIsInR...")));
    }
}
