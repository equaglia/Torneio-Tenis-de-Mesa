package com.eduq.quatoca.torneiotmapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

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

}
