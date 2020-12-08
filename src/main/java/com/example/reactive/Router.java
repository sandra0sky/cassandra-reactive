package com.example.reactive;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class Router {

    @Bean
    public RouterFunction<ServerResponse> route(UserHandler userHandler) {

        return RouterFunctions
                .route(GET("/users/hello"), userHandler::greet)
                .andRoute(GET("/users"), userHandler::getAllUsers)
                .andRoute(POST("/users/add"), userHandler::addUser);
    }

    @Bean
    RouterFunction<ServerResponse> routeWithEmail(UserHandler userHandler, TestEmailFilterFunction testFilterFunction) {

        return RouterFunctions
                .route(GET("/users/{email}"), userHandler::getUserByEmail)
                .andRoute(PUT("/users/update/{email}"), userHandler::updateUser)
                .filter(testFilterFunction);
    }

    //Q: when chaining filter function, are separate RouterFunctions needed if you want to apply a different filter function on different endpoints
    //Q: confused between when to apply validation in seperate validator or filter (eg trying to add something to check if email of attempted user already exists or not, if not add user, if yes return BAD_REQUEST + "email already exists"
    //TODO try to do a Basic Auth filter function
    //TODO add error handling

}
