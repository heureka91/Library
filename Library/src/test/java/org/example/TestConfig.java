package org.example;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfig {

    @Bean
    public DatabaseManager databaseManager() {
        return Mockito.mock(DatabaseManager.class);
    }
}
