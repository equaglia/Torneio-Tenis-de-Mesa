package com.eduq.quatoca.torneiotmapi.api.model;

import com.eduq.quatoca.torneiotmapi.domain.model.CategoriaJogador;

import lombok.Data;

@Data
public class JogadorModel {

	private Long id; // TODO Remover no futuro
	private String nome;
	private String sobrenome;
//	private CategoriaJogador categoria;

}
