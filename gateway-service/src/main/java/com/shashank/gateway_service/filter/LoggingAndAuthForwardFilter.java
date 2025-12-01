package com.shashank.gateway_service.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class LoggingAndAuthForwardFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        // Log path and method (simple)
        System.out.println("[GATEWAY] " + request.getMethod() + " " + request.getURI());
        // Authorization is forwarded automatically by default, but you can manipulate if needed:
        // String auth = request.getHeaders().getFirst("Authorization");
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
