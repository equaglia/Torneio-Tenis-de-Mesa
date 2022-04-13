package com.eduq.quatoca.torneiotmapi.api.model;

import java.util.Set;

import com.eduq.quatoca.torneiotmapi.domain.model.StatusJogo;

import lombok.Data;

@Data
public class PartidaResumoModel {

	private Long id;
//	private List<JogadorModel> jogadores;
	private Set<JogadorResumoModel> jogadores;
	private JogadorResumoModel jogadorPrimeiroSacador;
//	private List<GameModel> games;
//	private OffsetDateTime inicioPartida;
//	private OffsetDateTime fimPartida;
	private StatusJogo partidaStatus;

}
