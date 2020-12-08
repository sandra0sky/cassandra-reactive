package com.example.reactive;

import reactor.core.publisher.Mono;

public class UserValidator {

    private UserRepository repository;

    public UserValidator(UserRepository repository) {
        this.repository = repository;
    }

    public Mono<Boolean> userAlreadyExists(String email) {
        return repository.findById(email).map(found -> true).switchIfEmpty(Mono.just(false));
    }
}
