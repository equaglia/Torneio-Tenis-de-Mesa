package com.eduq.quatoca.torneiotmapi.api.model.input;

import javax.validation.constraints.NotBlank;

import com.eduq.quatoca.torneiotmapi.domain.model.CategoriaJogador;

import lombok.Data;

@Data
public class JogadorInput {

	@NotBlank
	private String nome;
	@NotBlank
	private String sobrenome;
	@NotBlank
	private CategoriaJogador categoria;

}
