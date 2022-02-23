package com.eduq.quatoca.torneiotmapi.api.model;

import java.time.OffsetDateTime;
import java.util.List;

import lombok.Data;

@Data
public class GameModel {

	private List<PontuacaoModel> pontos;
	private OffsetDateTime inicioGame;
	private OffsetDateTime fimGame;
}
