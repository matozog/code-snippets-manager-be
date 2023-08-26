package com.snippets.manager.snippetsmanager;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories("com.snippets.manager.snippetsmanager")
@EntityScan("com.snippets.manager.snippetsmanager.models")
@OpenAPIDefinition(info = @Info(title = "Code Snippets API", version = "2.0", description = "Code Snippets Information"))
@SpringBootApplication
public class SnippetsManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SnippetsManagerApplication.class, args);
	}

}
