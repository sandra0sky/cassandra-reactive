package com.example.reactive;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
public class UserService {

    private UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public Mono<List<User>> getAllUsers() { return repository.findAll().collectList(); }

    public Mono<User> findByEmail(String email) {
        return repository.findById(email);
    }

    public Mono<User> addUser(Mono<User> user) {
        return user.flatMap(repository::save);
    }

    //if it's SecurityException - create 403 ServerResponse
    //anything else, send generic BadRequest ServerResponse
}
