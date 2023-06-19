package com.eduq.quatoca.torneiotmapi.api.controller;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import com.eduq.quatoca.torneiotmapi.domain.model.Game;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.eduq.quatoca.torneiotmapi.api.dto.assembler.PartidaAssembler;
import com.eduq.quatoca.torneiotmapi.api.dto.assembler.PartidaResumoAssembler;
import com.eduq.quatoca.torneiotmapi.api.model.PartidaModel;
import com.eduq.quatoca.torneiotmapi.api.model.PartidaResumoModel;
import com.eduq.quatoca.torneiotmapi.domain.model.Partida;
import com.eduq.quatoca.torneiotmapi.domain.service.GestaoPartidaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/partidas")
@Tag(name = "PARTIDAS", description = "Controle de informações de partidas")
public class PartidaController {
	
	private PartidaAssembler partidaAssembler;
	private PartidaResumoAssembler partidaResumoAssembler;
	private GestaoPartidaService gestaoPartidaService;
	
	@Operation(summary = "Lista das partidas",
			description = "Listar as partidas da base de dados")
	@GetMapping
	public List<PartidaModel> listar() {
		return partidaAssembler.toCollectionModel(gestaoPartidaService.listar());
	}

	@Operation(summary = "Lista resumida das partidas",
			description = "Listar as informações resumidas das partidas da base de dados")
	@GetMapping("/resumo")
	public List<PartidaResumoModel> listarResumo() {
		return partidaResumoAssembler.toCollectionResumoModel(gestaoPartidaService.listar());
	}

	@Operation(summary = "Informações da partida",
			description = "Carregar as informações da partida")
	@GetMapping("/{partidaId}")
	public ResponseEntity<PartidaModel> buscar(
			@Parameter(description = "Identificador único da partida no BD") @PathVariable Long partidaId) {
		Partida partidas = gestaoPartidaService.buscar(partidaId);
		List<Game> games = partidas.getGames()
				.stream().sorted(Comparator.comparing(Game::getId))
				.collect(Collectors.toList());
		partidas.setGames(games);
		return Optional.of(partidas)
			.map(partida -> ResponseEntity.ok(partidaAssembler.toModel(partida)))
			.orElse(ResponseEntity.notFound().build());
	}

	@Operation(summary = "Remoção de partida",
			description = "Remover a partida da base de dados. Para ser removida, a partida deve estar no status CANCELADO.")
	@DeleteMapping("/{partidaId}")
	public ResponseEntity<Void> excluir(
			@Parameter(description = "Identificador único da partida no BD") @PathVariable Long partidaId) {
		gestaoPartidaService.excluirPartida(partidaId);
		return ResponseEntity.noContent().build();
	}

	@Operation(summary = "Preparação de partida",
			description = "Preparar partida, definindo seus jogadores")
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("/{jogadorAId}/{jogadorBId}/{quantidadeGames}")
	public ResponseEntity<PartidaModel> adicionar(
			@Parameter(description = "Identificador único do primeiro jogador no BD") @PathVariable Long jogadorAId, 
			@Parameter(description = "Identificador único do segundo jogador no BD") @PathVariable Long jogadorBId,
			@Parameter(description = "Quantidade de games da partida") @PathVariable int quantidadeGames) {
		return Optional.of(gestaoPartidaService.prepararPartida(jogadorAId, jogadorBId, quantidadeGames))
				.map(partida -> ResponseEntity.ok(partidaAssembler.toModel(partida)))
				.orElse(ResponseEntity.notFound().build());
	}
	
	@Operation(summary = "Início da partida",
			description = "Dar início a partida")
	@PutMapping("/{partidaId}/iniciar")
	public ResponseEntity<PartidaModel> iniciarPartida(
			@Parameter(description = "Identificador único da partida no BD") @PathVariable Long partidaId) {
		return Optional.of(gestaoPartidaService.iniciarPartida(partidaId))
				.map(partida -> ResponseEntity.ok(partidaAssembler.toModel(partida)))
				.orElse(ResponseEntity.notFound().build());
	}

	@Operation(summary = "Interrupição da partida",
			description = "Interromper a partida, se estiver em andamento")
	@PutMapping("/{partidaId}/interromper")
	public ResponseEntity<PartidaModel> interromperPartida(
			@Parameter(description = "Identificador único da partida no BD") @PathVariable Long partidaId) {
		return Optional.of(gestaoPartidaService.interromperPartida(partidaId))
				.map(partida -> ResponseEntity.ok(partidaAssembler.toModel(partida)))
				.orElse(ResponseEntity.notFound().build());
	}

	@Operation(summary = "Retornar partida interrompida",
			description = "Retornar andamento de partida, caso tenha sido interrompida e jogadores estejam disponíveis")
	@PutMapping("/{partidaId}/retornar")
	public ResponseEntity<PartidaModel> retornarPartidaInterrompida(
			@Parameter(description = "Identificador único da partida no BD") @PathVariable Long partidaId) {
		return Optional.of(gestaoPartidaService.retornarPartidaInterrompida(partidaId))
				.map(partida -> ResponseEntity.ok(partidaAssembler.toModel(partida)))
				.orElse(ResponseEntity.notFound().build());
	}

	@Operation(summary = "Continuação da partida",
			description = "Dar continuidade a partida, se tiver sido interrompida ou ao iniciar novo game")
	@PutMapping("/{partidaId}/continuar")
	public ResponseEntity<PartidaModel> continuarPartida(
			@Parameter(description = "Identificador único da partida no BD") @PathVariable Long partidaId) {
		return Optional.of(gestaoPartidaService.continuarPartida(partidaId))
				.map(partida -> ResponseEntity.ok(partidaAssembler.toModel(partida)))
				.orElse(ResponseEntity.notFound().build());
	}

	@Operation(summary = "Partida completa",
			description = "Completar todos os games e finalizar a partida")
	@PutMapping("/{partidaId}/completar")
	public ResponseEntity<PartidaModel> completarPartida(
			@Parameter(description = "Identificador único da partida no BD") @PathVariable Long partidaId,
			@Valid @RequestBody Partida partida) {//		@Valid @RequestBody List<Game> games) {
		return Optional.of(gestaoPartidaService.completarPontuacaoEFinalizarPartida(partidaId, partida))
				.map(partidaIn -> ResponseEntity.ok(partidaAssembler.toModel(partidaIn)))
				.orElse(ResponseEntity.notFound().build());
	}
	
	@Operation(summary = "Cancelamento de partida",
			description = "Cancelar a partida")
	@PutMapping("/{partidaId}/cancelar")
	public ResponseEntity<PartidaModel> cancelarPartida(
			@Parameter(description = "Identificador único da partida no BD") @PathVariable Long partidaId) {
		return Optional.of(gestaoPartidaService.cancelarPartida(partidaId))
				.map(partida -> ResponseEntity.ok(partidaAssembler.toModel(partida)))
				.orElse(ResponseEntity.notFound().build());
	}
}