package com.eduq.quatoca.torneiotmapi.api.controller;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.eduq.quatoca.torneiotmapi.api.assembler.GameAssembler;
import com.eduq.quatoca.torneiotmapi.api.model.GameModel;
import com.eduq.quatoca.torneiotmapi.domain.service.GestaoGameService;
import com.eduq.quatoca.torneiotmapi.domain.service.PontuacaoEmGameService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/games")
@Tag(name = "GAMES", description = "Controle de informações de games")
public class GameController {

	private GameAssembler gameAssembler;
	private GestaoGameService gestaoGameServiceImpl;
	private PontuacaoEmGameService pontuacaoEmGameService;

	@Operation(summary = "Lista de games",
			description = "Listar os games da partida ???") //TODO partida ou todos???
	@GetMapping
	public List<GameModel> listar() {
		return gameAssembler.toCollectionModel(gestaoGameServiceImpl.listar())
				.stream().sorted(Comparator.comparing(GameModel::getId))
				.collect(Collectors.toList());
	}
	
	@Operation(summary = "Informações do game",
			description = "Carregar as informações do game")
	@GetMapping("/{gameId}")
	public ResponseEntity<GameModel> buscar(
			@Parameter(description = "Identificador único do game no BD") @PathVariable Long gameId) {
		return Optional.of(gestaoGameServiceImpl.buscar(gameId))
				.map(game -> ResponseEntity.ok(gameAssembler.toModel(game)))
				.orElse(ResponseEntity.notFound().build());
	}
	
	@Operation(summary = "Atualização dos pontos dos jogadores",
			description = "Atualizar pontuação do game para ambos os jogadores")
	@PutMapping("/{gameId}/pontuar/{pontuacaoA}/{pontuacaoB}") 
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<GameModel> atualizarPontuacao(
			@Parameter(description = "Identificador único do game no BD") @PathVariable Long gameId, 
			@Parameter(description = "Identificador único da pontuacao do primeiro jogador no BD") @PathVariable int pontuacaoA,
			@Parameter(description = "Identificador único da pontuacao do segundo jogador no BD") @PathVariable int pontuacaoB) {
		return Optional.of(pontuacaoEmGameService.atualizarPontuacao(gameId, pontuacaoA, pontuacaoB))
				.map(game -> ResponseEntity.ok(gameAssembler.toModel(game)))
				.orElse(ResponseEntity.notFound().build());
	}

	@Operation(summary = "Força a finalização do game",
			description = "Atualizar pontuação de ambos os jogadores, forçando a finalização do game")
	@PutMapping("/finalizado/{gameId}/pontuar/{pontuacaoA}/{pontuacaoB}") 
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<GameModel> atualizarPontuacaoGameFinalizado(
			@Parameter(description = "Identificador único do game no BD") @PathVariable Long gameId, 
			@Parameter(description = "Identificador único da pontuacao do primeiro jogador no BD") @PathVariable int pontuacaoA,
			@Parameter(description = "Identificador único da pontuacao do segundo jogador no BD") @PathVariable int pontuacaoB) {
		return Optional.of(pontuacaoEmGameService.atualizarPontuacaoGameFinalizado(gameId, pontuacaoA, pontuacaoB))
				.map(game -> ResponseEntity.ok(gameAssembler.toModel(game)))
				.orElse(ResponseEntity.notFound().build());
	}

	@Operation(summary = "Soma de ponto a jogador",
			description = "Somar um ponto à pontuação de um dos jogadores")
	@PutMapping("/{gameId}/pontuar/{pontoId}/somar") 
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<GameModel> somarUmPonto(
			@Parameter(description = "Identificador único do game no BD") @PathVariable Long gameId, 
			@Parameter(description = "Identificador único da pontuacao do jogador no BD") @PathVariable Long pontoId) {
		return Optional.of(pontuacaoEmGameService.somaUmPonto(gameId, pontoId))
				.map(game -> ResponseEntity.ok(gameAssembler.toModel(game)))
				.orElse(ResponseEntity.notFound().build());
	}
	
	@Operation(summary = "Subtração de ponto a jogador",
			description = "Subtrair um ponto da pontuação de um dos jogadores")
	@PutMapping("/{gameId}/pontuar/{pontoId}/subtrair") 
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<GameModel> diminuirUmPonto(
			@Parameter(description = "Identificador único do game no BD") @PathVariable Long gameId, 
			@Parameter(description = "Identificador único da pontuacao do jogador no BD") @PathVariable Long pontoId) {
		return Optional.of(pontuacaoEmGameService.diminueUmPonto(gameId, pontoId))
				.map(game -> ResponseEntity.ok(gameAssembler.toModel(game)))
				.orElse(ResponseEntity.notFound().build());
	}
}