package com.jbs.pm;

import com.github.cloudyrock.spring.v5.EnableMongock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SuppressWarnings("PMD")
@SpringBootApplication
@EnableMongoRepositories
@EnableMongock
public class PmApplication {

  public static void main(String[] args) {
    SpringApplication.run(PmApplication.class, args);
  }
}
