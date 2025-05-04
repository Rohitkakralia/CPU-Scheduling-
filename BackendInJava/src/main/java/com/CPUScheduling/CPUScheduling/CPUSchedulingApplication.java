package com.CPUScheduling.CPUScheduling;


import com.CPUScheduling.CPUScheduling.controllers.ProcessController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CPUSchedulingApplication {

	public static void main(String[] args) {
		SpringApplication.run(CPUSchedulingApplication.class, args);
		ProcessController pr = new ProcessController();

		System.out.println(pr.result);
	}

}
