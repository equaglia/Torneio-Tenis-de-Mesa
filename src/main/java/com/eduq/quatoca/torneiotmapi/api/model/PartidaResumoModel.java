package com.eduq.quatoca.torneiotmapi.api.model;

import java.util.Set;

import com.eduq.quatoca.torneiotmapi.domain.model.StatusPartida;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartidaResumoModel {

	private Long id;
//	private List<JogadorModel> jogadores;
	private Set<JogadorResumoModel> jogadores;
//	private JogadorResumoModel jogadorPrimeiroSacador;
//	private List<GameModel> games;
//	private OffsetDateTime inicioPartida;
//	private OffsetDateTime fimPartida;
	private StatusPartida partidaStatus;

}
