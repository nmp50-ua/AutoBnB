package autobnb.config;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ArgonConfig {
    @Bean
    public Argon2 argon2() {
        return Argon2Factory.create();
    }
}
