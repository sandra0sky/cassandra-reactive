package com.example.reactive;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.mapping.Column;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public final class User {

    @Id
    private String email;
    @Column
    private String username;
    @Column
    private String firstname;
    @Column
    private String surname;
    @Column
    private int age;

}

