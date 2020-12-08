package com.example.reactive;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserValidatorTest {

    private static final User USER1 = User.builder().email("test1@hello.com").username("User1").firstname("jon1").surname("snow").age(100).build();

    @Mock
    private UserRepository repository;
    @InjectMocks
    private UserValidator validator;

    @Test
    public void userAlreadyExists_returnsTrueIfProvidedEmailIsFound() {
        String email = "test1@hello.com";
        given(repository.findById(email)).willReturn(Mono.just(USER1));

        StepVerifier.create(validator.userAlreadyExists(email))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    public void userAlreadyExists_returnsFalseIfProvidedEmailIsNotFound() {
        String email = "test3@hello.com";
        given(repository.findById(email)).willReturn(Mono.empty());

        StepVerifier.create(validator.userAlreadyExists(email))
                .expectNext(false)
                .verifyComplete();
    }

}