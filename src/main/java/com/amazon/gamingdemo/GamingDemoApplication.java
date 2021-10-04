package com.amazon.gamingdemo;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Actor Locator API", description = "An API to locate Actors in the game."))
public class GamingDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(GamingDemoApplication.class, args);
	}

}
