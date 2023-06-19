package com.eduq.quatoca.torneiotmapi.api.controller;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eduq.quatoca.torneiotmapi.api.dto.assembler.JogadorAssembler;
import com.eduq.quatoca.torneiotmapi.api.model.JogadorModel;
import com.eduq.quatoca.torneiotmapi.domain.service.ControleSacadorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/sacadores")
@Tag(name = "GAME/SACADOR", description = "Controle do primeiro sacador do game TESTE CODIGO")
public class SacadorController {

	private JogadorAssembler jogadorAssembler;
	private ControleSacadorService controleSacadorService;
	
	@GetMapping("/{partidaId}/games/{gameId}/sacador")//TODO partidaId parece não ser necessário - pode ser obtida através de gameId
	public ResponseEntity<JogadorModel> sacador(@PathVariable Long partidaId, @PathVariable Long gameId) {
		return Optional.of(controleSacadorService.getSacador(partidaId, gameId))
				.map(jogador -> ResponseEntity.ok(jogadorAssembler.toModel(jogador)))
				.orElse(ResponseEntity.notFound().build());
	}

	@Operation(summary = "Definição do sacador",
			description = "Definir o sacador do primeiro game da partida. O sacador de cada game será de acordo com o primeiro sacador, de forma que haja intercalação do sacador a cada novo game.")
	@PutMapping("/{partidaId}/primeiro-sacador/{jogadorId}")
	public void definirPrimeiroSacador(
			@Parameter(description = "Identificador único da partida no BD") @PathVariable Long partidaId, 
			@Parameter(description = "Identificador único do jogador no BD") @PathVariable Long jogadorId) {
		controleSacadorService.setPrimeiroSacador(partidaId, jogadorId);
	}
}
