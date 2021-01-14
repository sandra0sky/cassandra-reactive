package com.example.reactive;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Slf4j
@Component
public class UserHandler {

    private UserService userService;

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public Mono<ServerResponse> getAllUsers(ServerRequest request) {
        return userService.getAllUsers()
                .flatMap(s -> ServerResponse.ok().contentType(APPLICATION_JSON).body(Mono.just(s), User.class));
    }

    public Mono<ServerResponse> getUserByEmail(ServerRequest request) {
        return userService.findByEmail(request.pathVariable("email"))
                .flatMap(s -> ServerResponse.ok().contentType(APPLICATION_JSON).body(Mono.just(s), User.class));
    }

    public Mono<ServerResponse> addUser(ServerRequest request) {
        return userService.addUser(request.bodyToMono(User.class))
                .flatMap(s -> ServerResponse.accepted().contentType(APPLICATION_JSON).body(Mono.just(s), User.class));
    }

    public Mono<ServerResponse> updateUser(ServerRequest request) {
        return userService.updateUser(request.bodyToMono(User.class))
                .flatMap(s -> ServerResponse.ok().contentType(APPLICATION_JSON).body(Mono.just(s), User.class));
    }
}