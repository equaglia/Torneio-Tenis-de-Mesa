package com.eduq.quatoca.torneiotmapi.api.controller;

import java.util.Optional;

import com.eduq.quatoca.torneiotmapi.api.dto.assembler.PontuacaoAssembler;
import com.eduq.quatoca.torneiotmapi.api.model.PontuacaoModel;
import com.eduq.quatoca.torneiotmapi.domain.service.GestaoPontuacaoService;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/pontos")
@Tag(name = "PONTUAÇÃO", description = "Informações de pontuação")
public class PontuacaoController {

	private GestaoPontuacaoService gestaoPontuacaoService;

	private PontuacaoAssembler pontuacaoAssembler;
	
	@Operation(summary = "Informações da pontuação",
			description = "Carregar as informações da pontuação de um jogador")
	@GetMapping("/{pontuacaoId}")
	public ResponseEntity<PontuacaoModel> buscar(
			@Parameter(description = "Identificador único da pontuação no BD") @PathVariable Long gameId) {
		return Optional.of(gestaoPontuacaoService.buscar(gameId))
				.map(game -> ResponseEntity.ok(pontuacaoAssembler.toModel(game)))
				.orElse(ResponseEntity.notFound().build());
	}


//	TODO GET pontuacao da partida. TBD
//	TODO GET pontuacao do game. TBD
//	TODO GET pontuacao do jogador na partida. TBD
}
