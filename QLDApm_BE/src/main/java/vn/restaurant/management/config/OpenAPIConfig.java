package vn.restaurant.management.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    /**
     * Configures the OpenAPI bean to generate API documentation.
     *
     * @return OpenAPI Object used to define the API documentation information like title, version, and description.
     */
    @Bean
    public OpenAPI openAPI() {
        // Define the security scheme for the API (Bearer token)
        SecurityScheme securityScheme = new SecurityScheme()
                .name("Authorization")
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        // Attach the security scheme to all API endpoints
        SecurityRequirement securityRequirement = new SecurityRequirement().addList("Authorization");

        return new OpenAPI()
                .info(new Info()
                        .title("API Documentation")                // Title of the API documentation
                        .version("v1.0.0")                         // API version
                        .description("API Restaurant Management")         // Brief description of the API
                        .license(new License().name("API License") // License information with a link to the Swagger UI
                                .url("http://localhost:5000/swagger-ui/index.html")))
                .addSecurityItem(securityRequirement) // Add the security requirement
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("Authorization", securityScheme)) // Register the security scheme
                .servers(List.of(new Server()
                        .url("http://localhost:5000")              // Server URL
                        .description("Server Dev")));              // Description for the development server
    }


    /**
     * Configures the GroupedOpenApi bean to group API endpoints for version 1.
     *
     * @return GroupedOpenApi Object that defines the API group and the package to scan for controllers.
     */
    @Bean
    public GroupedOpenApi groupedOpenApi() {
        return GroupedOpenApi.builder()
                .group("api-v1")                                   // Name of the API group
                .packagesToScan("vn.restaurant.management.controller.rest") // Package where controllers are located
                .build();
    }

}
