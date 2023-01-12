package com.eduq.quatoca.torneiotmapi.api.model;

import java.util.List;

import com.eduq.quatoca.torneiotmapi.domain.model.enums.StatusPartida;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartidaResumoModel {

	private Long id;
//	private List<JogadorModel> jogadores;


	private List<JogadorResumoModel> jogadores;
//	private Set<JogadorResumoModel> jogadores;



	//	private JogadorResumoModel jogadorPrimeiroSacador;
//	private List<GameModel> games;
//	private OffsetDateTime inicioPartida;
//	private OffsetDateTime fimPartida;
	private StatusPartida partidaStatus;

}
