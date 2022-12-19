package com.eduq.quatoca.torneiotmapi.api.model;

import com.eduq.quatoca.torneiotmapi.domain.model.CategoriaJogador;
import com.eduq.quatoca.torneiotmapi.domain.model.StatusJogador;

import lombok.Data;

@Data
public class JogadorModel {

	private Long id; // TODO Remover no futuro
	private String nome;
	private String sobrenome;
	private StatusJogador status;
	private CategoriaJogador categoria;

}
