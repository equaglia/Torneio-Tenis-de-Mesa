package com.eduq.quatoca.torneiotmapi.api.controller;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.eduq.quatoca.torneiotmapi.api.assembler.JogadorAssembler;
import com.eduq.quatoca.torneiotmapi.api.assembler.PartidaAssembler;
import com.eduq.quatoca.torneiotmapi.api.assembler.PartidaResumoAssembler;
import com.eduq.quatoca.torneiotmapi.api.model.JogadorModel;
import com.eduq.quatoca.torneiotmapi.api.model.PartidaModel;
import com.eduq.quatoca.torneiotmapi.api.model.PartidaResumoModel;
import com.eduq.quatoca.torneiotmapi.domain.model.Partida;
import com.eduq.quatoca.torneiotmapi.domain.service.ControleSacadorService;
import com.eduq.quatoca.torneiotmapi.domain.service.GestaoPartidaService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/partidas")
public class PartidaController {
	
	private PartidaAssembler partidaAssembler;
	private PartidaResumoAssembler partidaResumoAssembler;
	private JogadorAssembler jogadorAssembler;
	private GestaoPartidaService gestaoPartidaService;
	private ControleSacadorService controleSacadorService;
	
	@GetMapping
	public List<PartidaModel> listar() {
		return partidaAssembler.toCollectionModel(gestaoPartidaService.listar());
	}

	@GetMapping("/resumo")
	public List<PartidaResumoModel> listarResumo() {
		return partidaResumoAssembler.toCollectionResumoModel(gestaoPartidaService.listar());
	}

	@GetMapping("/{partidaId}")
	public ResponseEntity<PartidaModel> buscar(@PathVariable Long partidaId) {
		return Optional.of(gestaoPartidaService.buscar(partidaId))
				.map(partida -> ResponseEntity.ok(partidaAssembler.toModel(partida)))
				.orElse(ResponseEntity.notFound().build());
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@RequestMapping("/{jogadorAId}/{jogadorBId}")
	public ResponseEntity<PartidaModel> adicionar(@PathVariable Long jogadorAId, @PathVariable Long jogadorBId) {
		return Optional.of(gestaoPartidaService.prepararPartida(jogadorAId, jogadorBId))
				.map(partida -> ResponseEntity.ok(partidaAssembler.toModel(partida)))
				.orElse(ResponseEntity.notFound().build());
	}
	
	@PutMapping
	@RequestMapping("/iniciarPartida/{partidaId}")
	public ResponseEntity<PartidaModel> iniciarPartida(@PathVariable Long partidaId) {
		return Optional.of(gestaoPartidaService.iniciarPartida(partidaId))
				.map(partida -> ResponseEntity.ok(partidaAssembler.toModel(partida)))
				.orElse(ResponseEntity.notFound().build());
	}

	@PutMapping
	@RequestMapping("/continuarPartida/{partidaId}")
	public ResponseEntity<PartidaModel> continuarPartida(@PathVariable Long partidaId) {
		return Optional.of(gestaoPartidaService.continuarPartida(partidaId))
				.map(partida -> ResponseEntity.ok(partidaAssembler.toModel(partida)))
				.orElse(ResponseEntity.notFound().build());
	}
	
	@PutMapping
	@RequestMapping("/partida/{partidaId}/primeiroSacador/{jogadorId}")
	public void definirPrimeiroSacador(@PathVariable Long partidaId, @PathVariable Long jogadorId) {
		controleSacadorService.definirPrimeiroSacador(partidaId, jogadorId);
	}
	
	@PutMapping
	@RequestMapping("partidaCompleta/{partidaId}")
	public ResponseEntity<PartidaModel> completarPartida(@PathVariable Long partidaId,
			@Valid @RequestBody Partida partidaIN) {//		@Valid @RequestBody List<Game> games) {
		return Optional.of(gestaoPartidaService.completarPontuacaoEFinalizarPartida(partidaId, partidaIN))
				.map(partida -> ResponseEntity.ok(partidaAssembler.toModel(partida)))
				.orElse(ResponseEntity.notFound().build());
	}
	
	@GetMapping
	@RequestMapping("/partida/{partidaId}/game/{gameId}")
	public ResponseEntity<JogadorModel> sacador(@PathVariable Long partidaId, @PathVariable Long gameId) {
		return Optional.of(controleSacadorService.getSacador(partidaId, gameId))
				.map(jogador -> ResponseEntity.ok(jogadorAssembler.toModel(jogador)))
				.orElse(ResponseEntity.notFound().build());
	}
}
