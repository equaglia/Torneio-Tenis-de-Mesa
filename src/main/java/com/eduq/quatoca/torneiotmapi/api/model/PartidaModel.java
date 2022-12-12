package com.eduq.quatoca.torneiotmapi.api.model;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

import com.eduq.quatoca.torneiotmapi.domain.model.StatusPartida;
import lombok.Data;

@Data
public class PartidaModel {

	private Long id;
//	private List<JogadorModel> jogadores;
	private Set<JogadorModel> jogadores;
	private OffsetDateTime inicioPartida;
	private OffsetDateTime fimPartida;
	private StatusPartida partidaStatus;
	private JogadorResumoModel jogadorPrimeiroSacador;
	private List<GameModel> games;
//	private Game gameAtual;

}
