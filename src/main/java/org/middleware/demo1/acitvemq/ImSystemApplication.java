package org.middleware.demo1.acitvemq;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

/**
 * @author Administrator
 */
@SpringBootApplication(scanBasePackages = {"org.middleware.demo1.acitvemq"})
@MapperScan("org.middleware.demo1.acitvemq.mapper")
@EnableScheduling
public class ImSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImSystemApplication.class, args);
    }

    @Bean
    ObjectMapper objectMapper(){
        return new ObjectMapper();
    }

    @Bean
    RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
