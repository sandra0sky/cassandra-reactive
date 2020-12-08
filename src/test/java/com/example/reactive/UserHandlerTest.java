package com.example.reactive;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = DemoApplication.class)
@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class UserHandlerTest {

    private static final User USER1 = User.builder().email("test1@hello.com").username("User1").firstname("jon1").surname("snow").age(100).build();
    private static final User USER2 = User.builder().email("test2@hello.com").username("User2").firstname("jon2").surname("snow").age(100).build();
    private static final User USER3 = User.builder().email("test3@hello.com").username("User3").firstname("jon3").surname("snow").age(100).build();
    private static final List<User> USER_LIST = List.of(USER1, USER2, USER3);

    @Autowired
    private WebTestClient testClient;
    @MockBean
    private UserService userService;
    @InjectMocks
    private UserHandler userHandler;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ServerRequest request;


    @Test
    void greet_isSuccessfulIfNameIsProvided() {
        given(request.queryParam("name")).willReturn(Optional.of("name1"));

        StepVerifier.create(userHandler.greet(request)).assertNext(serverResponse -> {
            assertThat(serverResponse).isNotNull();
            assertThat(serverResponse.statusCode()).isEqualTo(OK);
        }).verifyComplete();

    }

    @Test
    void greet_returnsFallbackIfNoNameIsProvided() {
        given(request.queryParam("name")).willReturn(Optional.empty());

        StepVerifier.create(userHandler.greet(request))
                .assertNext(serverResponse -> {
                    assertThat(serverResponse).isNotNull();
                    assertThat(serverResponse.statusCode()).isEqualTo(OK);
                }).verifyComplete();
    }

    @Test
    void getAllUsers_returnsListOfAllAvailableUsers() {
        given(userService.getAllUsers()).willReturn(Mono.just(USER_LIST));

        StepVerifier.create(userHandler.getAllUsers(request))
                .assertNext(serverResponse -> {
                    assertThat(serverResponse).isNotNull();
                    assertThat(serverResponse.statusCode()).isEqualTo(OK);
                }).verifyComplete();
    }

    @Test
    void getAllUsers_returns404IfNoUsersInDB() {
        given(userService.getAllUsers()).willReturn(Mono.empty());

        StepVerifier.create(userHandler.getAllUsers(request))
                .assertNext(serverResponse -> {
                    assertThat(serverResponse).isNotNull();
                    assertThat(serverResponse.statusCode()).isEqualTo(NOT_FOUND);
                }).verifyComplete();

    }

    @Test
    void getUserByEmail_returnsMatchingUser() {
        given(request.pathVariable("email")).willReturn(USER1.getEmail());
        given(userService.findByEmail(USER1.getEmail())).willReturn(Mono.just(USER1));

        StepVerifier.create(userHandler.getUserByEmail(request))
                .assertNext(serverResponse -> {
                    assertThat(serverResponse).isNotNull();
                    assertThat(serverResponse.statusCode()).isEqualTo(OK);
                }).verifyComplete();

    }

    @Test
    void getUserByEmail_returns404IfNoMatchingUserFoundInDB() {
        given(request.pathVariable("email")).willReturn("wrongEmail");
        given(userService.findByEmail("wrongEmail")).willReturn(Mono.empty());

        StepVerifier.create(userHandler.getUserByEmail(request))
                .assertNext(serverResponse -> {
                    assertThat(serverResponse).isNotNull();
                    assertThat(serverResponse.statusCode()).isEqualTo(NOT_FOUND);
                }).verifyComplete();

    }

    @Test
    void addUser_savesANewUserWithStatusCreatedAndReturnsUserBody() {
        given(userService.addUser(any())).willReturn(Mono.just(USER2));

        StepVerifier.create(userHandler.addUser(request))
                .assertNext(serverResponse -> {
                    assertThat(serverResponse).isNotNull();
                    assertThat(serverResponse.statusCode()).isEqualTo(CREATED);
                }).verifyComplete();

    }
}
