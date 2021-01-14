package com.example.reactive;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.List;

import static org.mockito.BDDMockito.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = DemoApplication.class)
@ExtendWith(SpringExtension.class)
public class IntegrationTest extends ApplicationTests {

    @Autowired
    private WebTestClient testClient;
    @MockBean
    private UserRepository repository;
    @InjectMocks
    private UserService service;

    @Test
    public void getAllUsers_returnsAFluxOfUsers() {

        List<User> userList = List.of(USER1, USER2, USER3);
        given(repository.findAll()).willReturn(Flux.fromIterable(userList));

        testClient.get()
                .uri("/users/")
                .header("Authorization", "Basic " +
                        Base64.getEncoder().encodeToString("username:password".getBytes()))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(User.class)
                .hasSize(3)
                .isEqualTo(userList);
    }

    @Test
    public void getAllUsers_returnsEmptyFluxIfNoUsersFound() {
        given(repository.findAll()).willReturn(Flux.empty());

        testClient.get()
                .uri("/users/")
                .header("Authorization", "Basic " +
                        Base64.getEncoder().encodeToString("username:password".getBytes()))
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    public void getUserByEmail_returnsMatchingUser() {
        given(repository.findById(USER1.getEmail())).willReturn(Mono.just(USER1));

        testClient.get()
                .uri(String.format("/users/%s", USER1.getEmail()))
                .header("Authorization", "Basic " +
                        Base64.getEncoder().encodeToString("username:password".getBytes()))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(User.class);
    }

    @Test
    public void getUserByEmail_returnsBadRequestIfNoUserMatching() {
        given(repository.findById("wrongEmail")).willReturn(Mono.empty());

        testClient.get()
                .uri(String.format("/users/%s", "wrongEmail"))
                .header("Authorization", "Basic " +
                        Base64.getEncoder().encodeToString("username:password".getBytes()))
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    public void addUser_savesUserAndReturnsCreatedAndUserBody() {
        given(repository.findById(USER2.getEmail())).willReturn(Mono.empty());
        given(repository.save(USER2)).willReturn(Mono.just(USER2));

        testClient.post()
                .uri("/users/add")
                .body(BodyInserters.fromValue(USER2))
                .header("Authorization", "Basic " +
                        Base64.getEncoder().encodeToString("username:password".getBytes()))
                .exchange()
                .expectStatus()
                .isAccepted()
                .expectBody(User.class);
    }

//    TODO test not working but functionality is
    @Test
    public void addUser_returnsBadRequestIfEmailAlreadyExistInDB() {
        given(repository.findById(USER2.getEmail())).willReturn(Mono.just(USER1));
        given(repository.save(USER2)).willReturn(Mono.empty());

        testClient.post()
                .uri("/users/add")
                .body(BodyInserters.fromValue(USER2))
                .header("Authorization", "Basic " +
                        Base64.getEncoder().encodeToString("username:password".getBytes()))
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    public void updateUser_savesUpdatedUserOverUserInDBAndReturnsOk() {
        given(repository.findById(USER3.getEmail())).willReturn(Mono.just(USER3));
        given(repository.save(USER3)).willReturn(Mono.just(USER3));

        testClient.put()
                .uri(String.format("/users/update"))
                .body(BodyInserters.fromValue(USER3))
                .header("Authorization", "Basic " +
                        Base64.getEncoder().encodeToString("username:password".getBytes()))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(User.class);
    }

    @Test
    public void updateUser_doesNotUpdateUserIfEmailNotMatching() {
        given(repository.findById("wrongEmail")).willReturn(Mono.empty());

        testClient.put()
                .uri(String.format("/users/update"))
                .body(BodyInserters.fromValue(User.builder().email("wrongEmail").build()))
                .header("Authorization", "Basic " +
                        Base64.getEncoder().encodeToString("username:password".getBytes()))
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    public void testBasicAuthHeader_authorizationSuccessfulWithCorrectCredentials() {
        given(repository.findAll()).willReturn(Flux.fromIterable(List.of(User.builder().build(), User.builder().build())));

        testClient.get()
                .uri("/users/")
                .header("Authorization", "Basic " +
                        Base64.getEncoder().encodeToString("username:password".getBytes()))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(User.class)
                .hasSize(2);
    }

    @Test
    public void testBasicAuthHeader_authorizationFailsWithSecurityExceptionWithIncorrectCredentials() {
        given(repository.findAll()).willReturn(Flux.fromIterable(List.of(User.builder().build(), User.builder().build())));

        testClient.get()
                .uri("/users/")
                .header("Authorization", "Basic " +
                        Base64.getEncoder().encodeToString("wrong-username:wrong-password".getBytes()))
                .exchange()
                .expectStatus()
                .isForbidden();
    }
}
