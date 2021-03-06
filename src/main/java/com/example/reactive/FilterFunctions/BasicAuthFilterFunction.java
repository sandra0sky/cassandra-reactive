package com.example.reactive.FilterFunctions;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFilterFunction;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Base64;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@Component
public class BasicAuthFilterFunction implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    private final String basicAuthCredentials;

    public BasicAuthFilterFunction() {
        this.basicAuthCredentials = createAuthHeader("username", "password");
    }

    @SneakyThrows
    @Override
    public Mono<ServerResponse> filter(ServerRequest request, HandlerFunction<ServerResponse> handlerFunction) {
        String authHeader = request.headers().asHttpHeaders().getFirst(AUTHORIZATION);
        if (!basicAuthCredentials.equals(authHeader)) {
            log.warn("Failed to authenticate user");
            return Mono.error(new SecurityException("Incorrect authorization credentials"));
        }
        return handlerFunction.handle(request);
    }

    private String createAuthHeader(String username, String password) {
        String usernameAndPassword = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(usernameAndPassword.getBytes());
    }

}
