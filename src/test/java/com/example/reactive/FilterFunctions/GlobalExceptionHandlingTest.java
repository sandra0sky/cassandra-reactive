package com.example.reactive.FilterFunctions;

import com.example.reactive.DemoApplication;
import com.example.reactive.User;
import com.example.reactive.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.Base64Utils;
import reactor.core.publisher.Flux;

import java.util.Base64;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.mockito.BDDMockito.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = DemoApplication.class)
@ExtendWith(SpringExtension.class)
class GlobalExceptionHandlingTest {

    @Autowired
    private WebTestClient testClient;
    @MockBean
    private UserRepository repository;

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
                .expectStatus().isForbidden();
    }
}