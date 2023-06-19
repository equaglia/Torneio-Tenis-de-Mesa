package com.eduq.quatoca.torneiotmapi.api.model.input;

import jakarta.validation.constraints.NotBlank;

// import com.eduq.quatoca.torneiotmapi.domain.model.enums.CategoriaJogador;

import lombok.Data;

@Data
public class JogadorInput { //TODO definir uso

	@NotBlank
	private String nome;
	@NotBlank
	private String sobrenome;
	@NotBlank
	private String categoria;
//	@NotBlank
//	private StatusJogador status;

}
