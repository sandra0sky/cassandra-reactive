package com.example.reactive.ErrorHandling;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

@Component
@Order(-2)
@Slf4j
public class GlobalExceptionHandler implements WebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable throwable) {
        if (throwable instanceof SecurityException) {
            logError(exchange, throwable);
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        }
        if (throwable instanceof IllegalArgumentException) {
            logError(exchange, throwable);
            exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
        }
        return Mono.empty();
    }

    private void logError(ServerWebExchange exchange, Throwable throwable) {
        log.error("Returning error on request path: {}, exception: {}, caused by: {}",
                exchange.getRequest().getPath(),
                throwable.getClass(),
                throwable.getMessage());
    }
}

