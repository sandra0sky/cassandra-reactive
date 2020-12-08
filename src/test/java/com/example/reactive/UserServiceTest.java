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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = DemoApplication.class)
@ExtendWith(SpringExtension.class)
public class UserServiceTest {

    @Autowired
    private WebTestClient testClient;
    private UserService userService;
    @MockBean
    private UserRepository repository;

    private static final User USER1 = User.builder().email("test1@hello.com").username("User1").firstname("jon1").surname("snow").age(100).build();
    private static final User USER2 = User.builder().email("test2@hello.com").username("User2").firstname("jon2").surname("snow").age(100).build();
    private static final User USER3 = User.builder().email("test3@hello.com").username("User3").firstname("jon3").surname("snow").age(100).build();


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
    public void getUserByEmail_returnsNotFoundIfNoUserMatching() {
        given(repository.findById("wrongEmail")).willReturn(Mono.empty());
        StepVerifier.create(userService.findByEmail("wrongEmail")).verifyComplete();
    }

    @Test
    public void addUser_savesUserAndReturnsOkAndUserBody() {
        given(repository.save(USER2)).willReturn(Mono.just(USER2));
        StepVerifier.create(userService.addUser(Mono.just(USER2))).expectNext(USER2).expectComplete().verify();
    }
}