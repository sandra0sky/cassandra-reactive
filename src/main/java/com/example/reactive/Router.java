package com.example.reactive;

import com.example.reactive.FilterFunctions.BasicAuthFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class Router {

    @Bean
    public RouterFunction<ServerResponse> route(UserHandler userHandler, BasicAuthFilterFunction basicAuthFilterFunction) {

        return RouterFunctions
                .route(GET("/users"), userHandler::getAllUsers)
                .andRoute(POST("/users/add"), userHandler::addUser)
                .andRoute(GET("/users/{email}"), userHandler::getUserByEmail)
                .andRoute(PUT("/users/update"), userHandler::updateUser)
                .filter(basicAuthFilterFunction);
    }
}
