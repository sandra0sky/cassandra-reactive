package com.example.reactive;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.BDDMockito.given;

@SpringBootTest
class DemoApplicationTests {

    private UserService userService;

    @MockBean
    private UserRepository repository;
    @Autowired
    private WebTestClient testClient;

	@Test
	void contextLoads() {
	}

    @Test
    public void getAllUsers_returnsAFluxOfUsers() {

        userService = new UserService(repository);

        List<User> userList =
                List.of(
                        (new User("test1@hello.com", "User1", "jon1", "snow", 100)),
                        (new User("test2@hello.com", "User2", "jon2", "snow", 100)),
                        (new User("test3@hello.com", "User3", "jon3", "snow", 100))
                );

        Flux<User> userFlux = Flux.fromIterable(userList);
        given(repository.findAll()).willReturn(userFlux);

        testClient.get()
                .uri("/users/")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(User.class)
                .hasSize(3)
                .isEqualTo(userList);

        StepVerifier.create(userFlux).expectComplete();
    }
}
