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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = DemoApplication.class)
@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class UserHandlerTest extends ApplicationTests{

    @Autowired
    private WebTestClient testClient;
    @Mock
    private UserService userService;
    @MockBean
    UserRepository repository;
    @InjectMocks
    private UserHandler userHandler;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ServerRequest request;

    private static final List<User> USER_LIST = List.of(USER1, USER2, USER3);

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
    void getUserByEmail_returnsMatchingUser() {
        given(request.pathVariable("email")).willReturn(USER1.getEmail());
        given(userService.findByEmail(USER1.getEmail())).willReturn(Mono.just(USER1));

        StepVerifier.create(userHandler.getUserByEmail(request))
                .assertNext(serverResponse -> {
                    assertThat(serverResponse).isNotNull();
                    assertThat(serverResponse.statusCode()).isEqualTo(OK);
                }).verifyComplete();
    }

    //TODO failing
    @Test
    void getUserByEmail_returnsBadRequestIfNoMatchingUserFoundInDB() {
        given(request.pathVariable("email")).willReturn("wrongEmail");
        given(userService.findByEmail("wrongEmail")).willReturn(Mono.error(new IllegalArgumentException()));

        StepVerifier.create(userHandler.getUserByEmail(request))
                .assertNext(serverResponse -> {
                    assertThat(serverResponse).isNotNull();
                    assertThat(serverResponse.statusCode()).isEqualTo(BAD_REQUEST);
                }).verifyComplete();
    }

    @Test
    void addUser_savesANewUserWithStatusCreatedAndReturnsUserBody() {
        given(userService.addUser(any())).willReturn(Mono.just(USER2));

        StepVerifier.create(userHandler.addUser(request))
                .assertNext(serverResponse -> {
                    assertThat(serverResponse).isNotNull();
                    assertThat(serverResponse.statusCode()).isEqualTo(ACCEPTED);
                }).verifyComplete();
    }

    @Test
    void updateUser_updatesExistingUserReturnsOkAndUserBody() {
        given(userService.updateUser(any())).willReturn(Mono.just(USER2));

        StepVerifier.create(userHandler.updateUser(request))
                .assertNext(serverResponse -> {
                    assertThat(serverResponse).isNotNull();
                    assertThat(serverResponse.statusCode()).isEqualTo(OK);
                }).verifyComplete();
    }

    //TODO failing
    @Test
    void updateUser_returnsBadRequestIfEmailNotMatching() {
        given(userService.updateUser(any())).willReturn(Mono.error(new IllegalArgumentException()));

        StepVerifier.create(userHandler.updateUser(request))
                .assertNext(serverResponse -> {
                    assertThat(serverResponse.statusCode()).isEqualTo(BAD_REQUEST);
                }).verifyComplete();
    }
}
