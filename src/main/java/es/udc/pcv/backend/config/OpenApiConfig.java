package es.udc.pcv.backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customConfiguration(){
        return new OpenAPI().components(new Components()).info(new Info().title("PCV API").
                description("Rest api documentation").version("3.0.0"));
    }
}
