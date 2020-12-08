package com.example.reactive;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFilterFunction;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Optional;
//TODO not working atm

@Component
public class UserExistsFilterFunction implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    private UserRepository userRepository;

    public UserExistsFilterFunction(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Mono<ServerResponse> filter(ServerRequest serverRequest,
                                       HandlerFunction<ServerResponse> handlerFunction) {

        Mono<User> newUserMono = serverRequest.bodyToMono(User.class);
        String newEmail = newUserMono.map(User::getEmail).toString();
        Mono<User> existingUserMono = userRepository.findById(newEmail);
        Optional<String> existingEmail = Optional.of(existingUserMono.map(User::getEmail).toString());

        if (!existingEmail.get().isEmpty()) {
            return ServerResponse.status(HttpStatus.BAD_REQUEST).build();
        }
        return handlerFunction.handle(serverRequest);
    }
}