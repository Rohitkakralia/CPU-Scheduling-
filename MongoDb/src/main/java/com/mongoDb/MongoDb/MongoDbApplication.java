package com.mongoDb.MongoDb;

import com.mongoDb.MongoDb.controllers.ProcessController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MongoDbApplication {

	public static void main(String[] args) {
		SpringApplication.run(MongoDbApplication.class, args);
		ProcessController pr = new ProcessController();

		System.out.println(pr.result);
	}

}
