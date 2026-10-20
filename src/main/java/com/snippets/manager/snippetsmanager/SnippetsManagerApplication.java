package com.snippets.manager.snippetsmanager;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
		info = @Info(title = "Code Snippets API", version = "2.0", description = "Code Snippets Information"),
		security = @SecurityRequirement(name = "google"))
// lets Swagger UI send a Google ID token ("Authorize" button)
@SecurityScheme(name = "google", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
@SpringBootApplication
public class SnippetsManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SnippetsManagerApplication.class, args);
	}

}
