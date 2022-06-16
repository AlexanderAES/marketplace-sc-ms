package alexandersuetnov.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

public class ProxyConfig {
    @Bean
    RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user_service",
                        route -> route.path("/users/**")
                                .uri("lb://user-service"))
//                .route("product_route",
//                        route -> route.path("/products/**")
//                                .filters(filter -> filter.stripPrefix(1)
//                                )
//                                .uri("lb://product-service"))
                .build();
    }
}
