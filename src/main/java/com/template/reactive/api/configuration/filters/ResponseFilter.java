package com.template.reactive.api.configuration.filters;

import com.template.reactive.api.common.AppConstants;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class ResponseFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        exchange.getResponse().getHeaders().add(AppConstants.TRACE_ID_HEADER, MDC.get("traceId"));
        return chain.filter(exchange);
    }
}