package com.example.reactive;


import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration;
import org.springframework.boot.autoconfigure.data.cassandra.CassandraDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.cassandra.CassandraReactiveDataAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.cassandra.SessionFactory;
import org.springframework.data.cassandra.core.convert.CassandraCustomConversions;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public abstract class ApplicationTests {

    public static final User USER1 = User.builder().email("test1@hello.com").username("User1").firstname("jon1").surname("snow").age(99).build();
    public static final User USER2 = User.builder().email("test2@hello.com").username("User2").firstname("jon2").surname("snow").age(55).build();
    public static final User USER3 = User.builder().email("test3@hello.com").username("User3").firstname("jon3").surname("snow").age(22).build();

    @MockBean
    CassandraDataAutoConfiguration configuration;
    @MockBean
    CassandraCustomConversions conversions;
    @MockBean
    SessionFactory sessionFactory;
    @MockBean
    CassandraReactiveDataAutoConfiguration reactiveCassandraSession;
    @MockBean
    CqlSessionBuilder cqlSessionBuilder;
    @MockBean
    CqlSession cqlSession;
}