package com.example.reactive;

import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;

public interface UserRepository extends ReactiveCassandraRepository<User, String> {
}
