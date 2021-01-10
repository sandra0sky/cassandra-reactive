//package com.example.reactive.ErrorHandling;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.annotation.Order;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Component;
//import org.springframework.web.reactive.function.server.ServerResponse;
//import org.springframework.web.server.ResponseStatusException;
//import org.springframework.web.server.ServerWebExchange;
//import org.springframework.web.server.WebExceptionHandler;
//import reactor.core.publisher.Mono;
//
//@Component
//@Order(-2)
//@Slf4j
//public class GlobalExceptionHandler implements WebExceptionHandler {
//
//    @Override
//    public Mono<Void> handle(ServerWebExchange exchange, Throwable throwable) {
//        if (throwable instanceof SecurityException) {
//            logError(exchange, throwable);
//            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
//        }
//        return Mono.empty();
//    }
//
//    private void logError(ServerWebExchange exchange, Throwable throwable) {
//        log.error("Returning error on request path: {}, caused by: {}",
//                exchange.getRequest().getPath(),
//                throwable.getMessage());
//    }
//}
//
