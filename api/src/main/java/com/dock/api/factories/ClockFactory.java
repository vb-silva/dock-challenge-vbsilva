package com.dock.api.factories;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class ClockFactory {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
