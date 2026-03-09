package com.winderp.dashbordservice.config;



import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI dashboardAPI(){
        return new OpenAPI()
                .info(new Info()
                        .title("WindERP Dashboard Service")
                        .description("Statistiques et rapports du recrutement")
                        .version("1.0"))
                .externalDocs(new ExternalDocumentation()
                        .description("WindERP Documentation"));
    }
}