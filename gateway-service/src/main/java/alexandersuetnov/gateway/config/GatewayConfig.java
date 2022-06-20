package alexandersuetnov.gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Autowired
    AuthenticationFilter filter;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user-service", r -> r.path("/users/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://user-service"))
                .route("product-service", r -> r.path("/products/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://product-service"))
                .route("image-service", r -> r.path("/images/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://image-service"))

                .build();
    }

}
