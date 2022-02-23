package com.eduq.quatoca.torneiotmapi.api.model;

import java.time.OffsetDateTime;
import java.util.List;

import lombok.Data;

@Data
public class PartidaModel {

//	private Long id;
	private List<JogadorModel> jogadores;
	private List<GameModel> games;
	private OffsetDateTime inicioPartida;
	private OffsetDateTime fimPartida;

}
