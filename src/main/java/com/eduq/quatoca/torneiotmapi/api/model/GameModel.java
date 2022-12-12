package com.eduq.quatoca.torneiotmapi.api.model;

import java.time.OffsetDateTime;
import java.util.List;

import com.eduq.quatoca.torneiotmapi.domain.model.StatusGame;

import lombok.Data;

@Data
public class GameModel {

	private Long id;
	private List<PontuacaoModel> pontos;
	private OffsetDateTime inicioGame;
	private OffsetDateTime fimGame;
	private StatusGame gameStatus;
}
