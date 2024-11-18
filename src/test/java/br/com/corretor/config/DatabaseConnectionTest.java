            package br.com.corretor.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import io.r2dbc.spi.ConnectionFactory;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import io.r2dbc.spi.Result;

@SpringBootTest
public class DatabaseConnectionTest {

    @Autowired
    private ConnectionFactory connectionFactory;

    @Test
    public void testDatabaseConnection() {
        Mono<Void> connectionMono = Mono.from(connectionFactory.create())
            .flatMap(connection -> Mono.from(connection.close()));
        
        StepVerifier.create(connectionMono)
            .verifyComplete();
    }

    @Test
    public void testDatabaseQuery() {
        Mono<Result> resultMono = Mono.from(connectionFactory.create())
            .flatMap(connection -> 
                Mono.from(connection.createStatement("SELECT 1").execute())
                    .doFinally(signalType -> connection.close())
            );
        
        StepVerifier.create(resultMono)
            .expectNextCount(1)
            .verifyComplete();
    }
}
