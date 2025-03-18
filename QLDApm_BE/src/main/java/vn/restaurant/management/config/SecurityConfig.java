package vn.restaurant.management.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private static final String[] PUBLIC_ENDPOINTS_POST = {
            "/api/v1/login", "/api/v1/logout", "/api/v1/user",
            "/api/v1/refresh","/api/v1/introspect",

            "/api/v1/search/dishes/*"

    };

    private static final String[] PUBLIC_ENDPOINTS_GET = {
            "/api/v1/dishes","/api/v1/dish/*", "/api/v1/categories",
            "/api/v1/category","/api/v1/tables", "/api/v1/table/*",
            "/api/v1/table/available/today", "/api/v1/reservation",
            "/api/v1/reservation/*"
    };

    private static final String[] PUBLIC_ENDPOINTS_PUT = {
    };

    private static final String[] PUBLIC_ENDPOINTS_DELETE = {
    };

    private static final String[] SWAGGER_WHITELIST = {
            "/swagger-ui/**","/swagger-ui.html","/v3/api-docs/**","/api-docs/**"
    };

    @Autowired
    private CustomJwtDecoder customJwtDecoder;

    public SecurityConfig(CustomJwtDecoder customJwtDecoder) {
        this.customJwtDecoder = customJwtDecoder;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.cors().and().authorizeHttpRequests(authorize -> authorize
                .requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINTS_POST).permitAll()
                .requestMatchers(HttpMethod.GET, PUBLIC_ENDPOINTS_GET).permitAll()
                .requestMatchers(HttpMethod.DELETE, PUBLIC_ENDPOINTS_DELETE).permitAll()
                .requestMatchers(HttpMethod.PUT, PUBLIC_ENDPOINTS_PUT).permitAll()
                .requestMatchers(SWAGGER_WHITELIST).permitAll()
                .anyRequest().authenticated()
        );

        httpSecurity.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtConfigurer -> jwtConfigurer
                        .decoder(customJwtDecoder)
                        .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                .authenticationEntryPoint(new JwtAuthenticationEntryPoint()));
        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        return httpSecurity.build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }

    @Bean
    GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults(""); // Remove the "ROLE_" prefix
    }
}