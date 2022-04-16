package com.eduq.quatoca.torneiotmapi.api.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.eduq.quatoca.torneiotmapi.api.assembler.GameAssembler;
import com.eduq.quatoca.torneiotmapi.api.model.GameModel;
import com.eduq.quatoca.torneiotmapi.domain.model.Game;
import com.eduq.quatoca.torneiotmapi.domain.service.GestaoGameService;
import com.eduq.quatoca.torneiotmapi.domain.service.PontuacaoEmGameService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/games")
public class GameController {

	private GameAssembler gameAssembler;
	private GestaoGameService gestaoGameService;
	private PontuacaoEmGameService pontuacaoEmGameService;

	@GetMapping
	public List<GameModel> listar() {
		return gameAssembler.toCollectionModel(gestaoGameService.listar());
	}
	
	@GetMapping("/{gameId}")
	public ResponseEntity<GameModel> buscar(@PathVariable Long gameId) {
		return Optional.of(gestaoGameService.buscar(gameId))
				.map(game -> ResponseEntity.ok(gameAssembler.toModel(game)))
				.orElse(ResponseEntity.notFound().build());
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Game adicionar() {
		return gestaoGameService.salvar(new Game());
	}
	
	@PutMapping("/gameId/{gameId}/pontos/{pontuacaoA}/{pontuacaoB}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<GameModel> atualizarPontuacao(
			@PathVariable Long gameId, 
			@PathVariable int pontuacaoA,
			@PathVariable int pontuacaoB) {
		return Optional.of(pontuacaoEmGameService.atualizarPontuacao(gameId, pontuacaoA, pontuacaoB))
				.map(game -> ResponseEntity.ok(gameAssembler.toModel(game)))
				.orElse(ResponseEntity.notFound().build());
	}

	@PutMapping("/finalizado/gameId/{gameId}/pontos/{pontuacaoA}/{pontuacaoB}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<GameModel> atualizarPontuacaoGameFinalizado(
			@PathVariable Long gameId, 
			@PathVariable int pontuacaoA,
			@PathVariable int pontuacaoB) {
		return Optional.of(pontuacaoEmGameService.atualizarPontuacaoGameFinalizado(gameId, pontuacaoA, pontuacaoB))
				.map(game -> ResponseEntity.ok(gameAssembler.toModel(game)))
				.orElse(ResponseEntity.notFound().build());
	}

	@PutMapping("/soma/gameId/{gameId}/pontoId/{pontoId}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<GameModel> somarUmPonto(
			@PathVariable Long gameId, 
			@PathVariable Long pontoId) {
		return Optional.of(pontuacaoEmGameService.somaUmPonto(gameId, pontoId))
				.map(game -> ResponseEntity.ok(gameAssembler.toModel(game)))
				.orElse(ResponseEntity.notFound().build());
	}
	
	@PutMapping("/diminue/gameId/{gameId}/pontoId/{pontoId}")
	@ResponseStatus(HttpStatus.OK)
	public Game diminuirUmPonto(
			@PathVariable Long gameId, 
			@PathVariable Long pontoId) {
		return pontuacaoEmGameService.diminueUmPonto(gameId, pontoId);
	}
	
}



















