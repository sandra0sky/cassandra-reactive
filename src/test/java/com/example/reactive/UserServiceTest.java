package com.example.reactive;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.BDDMockito.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = DemoApplication.class)
@ExtendWith(SpringExtension.class)
public class UserServiceTest extends ApplicationTests {

    @Autowired
    private WebTestClient testClient;
    @MockBean
    private UserRepository repository;

    private UserService userService;

    @BeforeEach
    public void setUp() {
        userService = new UserService(repository);
    }

    @Test
    public void getAllUsers_returnsAFluxOfUsersAndCollectsToList() {
        given(repository.findAll()).willReturn(Flux.just(USER1, USER2, USER3));
        StepVerifier.create(userService.getAllUsers()).expectNext(List.of(USER1, USER2, USER3)).expectComplete().verify();
    }

    @Test
    public void getUserByEmail_returnsMatchingUser() {
        given(repository.findById(USER1.getEmail())).willReturn(Mono.just(USER1));
        StepVerifier.create(userService.findByEmail(USER1.getEmail())).expectNext(USER1).expectComplete().verify();
    }

    @Test
    public void getUserByEmail_returnsErrorIfEmailNotFound() {
        given(repository.findById("wrongEmail")).willReturn(Mono.empty());
        StepVerifier.create(userService.findByEmail("wrongEmail")).expectError().verify();
    }

    @Test
    public void addUser_savesUser() {
        given(repository.findById(USER2.getEmail())).willReturn(Mono.empty());
        given(repository.save(USER2)).willReturn(Mono.just(USER2));
        StepVerifier.create(userService.addUser(Mono.just(USER2))).expectNext(USER2).expectComplete().verify();
    }

    @Test
    public void addUser_returnsErrorIfNewUsersEmailIsAlreadyInDB() {
        given(repository.findById(USER2.getEmail())).willReturn(Mono.just(USER1));
        given(repository.save(USER2)).willReturn(Mono.empty());
        StepVerifier.create(userService.addUser(Mono.just(USER2))).expectError().verify();
    }

    @Test
    public void updateUser_savesUpdatedUser() {
        given(repository.findById(USER2.getEmail())).willReturn(Mono.just(USER2));
        given(repository.save(USER2)).willReturn(Mono.just(USER2));
        StepVerifier.create(userService.updateUser(Mono.just(USER2))).expectNext(USER2).expectComplete().verify();
    }

    @Test
    public void updateUser_returnsErrorIfEmailToUpdateUserIsNotFound() {
        given(repository.findById(USER2.getEmail())).willReturn(Mono.empty());
        given(repository.save(USER2)).willReturn(Mono.empty());
        StepVerifier.create(userService.updateUser(Mono.just(USER2))).expectError().verify();
    }
}