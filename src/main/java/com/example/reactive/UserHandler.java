package com.example.reactive;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    public Mono<ServerResponse> greet(ServerRequest request) {
        return sayHello(request)
                .onErrorReturn("Hello stranger")
                .flatMap(s -> ServerResponse.status(HttpStatus.OK).contentType(MediaType.TEXT_PLAIN).bodyValue(s));
    }

    public Mono<ServerResponse> getAllUsers(ServerRequest request) {
        return userService.getAllUsers()
                .flatMap(s -> ServerResponse.status(HttpStatus.OK).contentType(APPLICATION_JSON).body(Mono.just(s), User.class))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> getUserByEmail(ServerRequest request) {
        return userService.findByEmail(request.pathVariable("email"))
                .flatMap(s -> ServerResponse.status(HttpStatus.OK).contentType(APPLICATION_JSON).body(Mono.just(s), User.class))
                .switchIfEmpty(ServerResponse.status(HttpStatus.NOT_FOUND).build());
    }

    public Mono<ServerResponse> addUser(ServerRequest request) {
        return userService.addUser(request.bodyToMono(User.class))
                .flatMap(s -> ServerResponse.status(HttpStatus.CREATED).contentType(APPLICATION_JSON).body(Mono.just(s), User.class));
    }

    public Mono<ServerResponse> updateUser(ServerRequest request) {
        Mono<User> existingUserMono = userService.findByEmail(request.pathVariable("email"));
        Mono<User> updatedUserMono = request.bodyToMono(User.class);

        return updatedUserMono.zipWith(existingUserMono,
                (updatedUser, existingUser) ->
                        User.builder()
                                .email(existingUser.getEmail())
                                .username(updatedUser.getUsername())
                                .firstname(updatedUser.getFirstname())
                                .surname(updatedUser.getSurname())
                                .age(updatedUser.getAge()).build())
                .flatMap(updatedUser ->
                        ServerResponse.ok()
                                .contentType(APPLICATION_JSON)
                                .body(userService.addUser(Mono.just(updatedUser)), User.class))
                .switchIfEmpty(ServerResponse.status(HttpStatus.NOT_FOUND).build());
    }

    private Mono<String> sayHello(ServerRequest request) {
        if (request.queryParam("name").isPresent()) {
            String queryParamName = request.queryParam("name").get();
            log.info("Received name " + queryParamName);
            return queryParamName.equals("sandra") ? Mono.just("Hello admin") : Mono.just("Hello " + queryParamName);
        } else {
            log.error("Did not receive a name");
            return Mono.error(() -> new IllegalArgumentException("failed getting input"));
        }
    }
}