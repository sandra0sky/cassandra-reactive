package com.example.reactive;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
public class IntegrationTest {

    @Autowired
    private WebTestClient testClient;
    @MockBean
    private UserRepository repository;
    @MockBean
    private UserValidator validator;

    private static final User USER1 = User.builder().email("test1@hello.com").username("User1").firstname("jon1").surname("snow").age(100).build();
    private static final User USER2 = User.builder().email("test2@hello.com").username("User2").firstname("jon2").surname("snow").age(100).build();
    private static final User USER3 = User.builder().email("test3@hello.com").username("User3").firstname("jon3").surname("snow").age(100).build();

    @Test
    void greet_isSuccessfulAndGreetsAdminIfAdminNameProvided() {

        testClient.get().uri(uriBuilder -> uriBuilder
                .path("/users/hello")
                .queryParam("name", "sandra").build())
                .header("Authorization", "Basic " +
                        Base64.getEncoder().encodeToString("username:password".getBytes()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("Hello admin");
    }

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
    public void getUserByEmail_returnsNotFoundIfNoUserMatching() {
        given(repository.findById("wrongEmail")).willReturn(Mono.empty());

        testClient.get()
                .uri(String.format("/users/%s", "wrongEmail"))
                .header("Authorization", "Basic " +
                        Base64.getEncoder().encodeToString("username:password".getBytes()))
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    public void addUser_savesUserAndReturnsOkAndUserBody() {
        given(repository.save(USER2)).willReturn(Mono.just(USER2));

        testClient.post()
                .uri("/users/add")
                .body(BodyInserters.fromValue(USER2))
                .header("Authorization", "Basic " +
                        Base64.getEncoder().encodeToString("username:password".getBytes()))
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(User.class);
    }
//
//    @Test
//    public void addUser_returnsBadRequestIfUserEmailAlreadyExists() {
//        User overRideUser = User.builder().email("test2@hello.com").username("NewUser").firstname("NewUser").surname("SoNew").age(1).build();
//
//        given(repository.save(USER2)).willReturn(Mono.just(USER2));
//
//        testClient.post()
//                .uri("/users/add")
//                .exchange()
//                .expectStatus()
//                .isBadRequest();
//    }

    @Test
    public void updateUser_savesUpdatedUserOverUserInDBAndReturnsOk() {
        given(repository.findById(USER3.getEmail())).willReturn(Mono.just(USER3));
        given(repository.save(USER3)).willReturn(Mono.just(USER3));

        testClient.put()
                .uri(String.format("/users/update/%s", USER3.getEmail()))
                .body(BodyInserters.fromValue(USER3))
                .header("Authorization", "Basic " +
                        Base64.getEncoder().encodeToString("username:password".getBytes()))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(User.class);
    }

    @Test
    public void updateUser_doesNotSaveUserIfEmailNotMatching() {
        given(repository.findById("wrongEmail")).willReturn(Mono.empty());

        testClient.put()
                .uri(String.format("/users/%s/update", "wrongEmail"))
                .header("Authorization", "Basic " +
                        Base64.getEncoder().encodeToString("username:password".getBytes()))
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    public void whenUserEmailIsTest_thenHandlerFunctionIsApplied() {
        testClient.get()
                .uri(String.format("/users/%s/", "test"))
                .header("Authorization", "Basic " +
                        Base64.getEncoder().encodeToString("username:password".getBytes()))
                .exchange()
                .expectStatus()
                .isForbidden();
    }
}
