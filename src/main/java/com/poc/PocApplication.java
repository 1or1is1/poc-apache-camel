package com.poc;

import com.poc.producer.MockKafkaProducer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PocApplication {

	public static void main(String[] args) {
		SpringApplication.run(PocApplication.class, args);
	}

	@Bean
	public CommandLineRunner runner(MockKafkaProducer kafkaProducer) {
		return (args) -> kafkaProducer.produceMessages();
	}

}
