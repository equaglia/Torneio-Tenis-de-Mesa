package com.eduq.quatoca.torneiotmapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.Arrays;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(
		title = "Partida de Tênis de Mesa API", 
		version = "0.1", 
		description = "Informações da Partida",
		contact = @Contact(
				name = "Eduardo Quaglia",
				email = "equaglia@gmail.com",
				url = "QUATOCA"),//TODO definir url para quatoca
		license = @License(
				//url = "http://www.apache.org/licenses/LICENSE-2.0.html",
				//name = "Apache 2.0"
				url = "TBD",
				name = "TBD")))//TODO Definir license
public class TorneioTmApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(TorneioTmApiApplication.class, args);
	}

/*	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/jogadores").allowedOrigins("http://localhost:4200");
			}
		};
	}*/

	@Bean
	public CorsFilter corsFilter() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");
		config.setMaxAge(3600L);
		config.setAllowedOrigins(Arrays.asList("http://localhost:4200"));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);

		return new CorsFilter(source);
	}
}

