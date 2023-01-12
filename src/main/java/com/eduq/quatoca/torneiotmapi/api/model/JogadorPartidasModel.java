package com.eduq.quatoca.torneiotmapi.api.model;

import java.util.List;

import com.eduq.quatoca.torneiotmapi.domain.model.enums.CategoriaJogador;

import lombok.Data;

@Data
public class JogadorPartidasModel {

	private Long id; // Remover no futuro
	private String nome;
	private String sobrenome;
	private List<PartidaModel> partidas;
	private CategoriaJogador categoria;

}
