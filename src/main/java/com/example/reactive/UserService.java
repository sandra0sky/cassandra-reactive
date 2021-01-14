package com.example.reactive;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
public class UserService {

    private UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public Mono<List<User>> getAllUsers() {
        return repository.findAll().collectList();
    }

    public Mono<User> findByEmail(String email) {
        return repository.findById(email)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found")))
                .doOnError(error -> log.error("User {} not found", email));
    }

    public Mono<User> addUser(Mono<User> newUserMono) {
        return newUserMono.flatMap(newUser ->
                repository
                        .findById(newUser.getEmail())
                        .doOnNext(logger -> log.error("found a user"))
                        .doOnError(logger -> log.error("this is error"))
                        .flatMap(existingUser -> Mono.<User>error(new IllegalArgumentException("User already exists")))
                        .switchIfEmpty(repository.save(newUser))
                        .doOnError(error -> log.error("User {} already exists", newUser.getEmail())));
    }

    public Mono<User> updateUser(Mono<User> updatedUserMono) {
        return updatedUserMono
                .flatMap(updatedUser ->
                        repository
                                .findById(updatedUser.getEmail())
                                .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found")))
                                .doOnError(error -> log.error("User {} not found", updatedUser.getEmail()))
                                .map(oldUser ->
                                        User.builder()
                                                .email(updatedUser.getEmail())
                                                .username(updatedUser.getUsername())
                                                .firstname(updatedUser.getFirstname())
                                                .surname(updatedUser.getSurname())
                                                .age(updatedUser.getAge()).build()))
                .flatMap(updatedUser -> repository.save(updatedUser));
    }
}
