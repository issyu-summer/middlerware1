package org.middleware.demo1.acitvemq;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @author Administrator
 */
@SpringBootApplication
public class ImSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImSystemApplication.class, args);
    }

    @Bean
    ObjectMapper objectMapper(){
        return new ObjectMapper();
    }

}
