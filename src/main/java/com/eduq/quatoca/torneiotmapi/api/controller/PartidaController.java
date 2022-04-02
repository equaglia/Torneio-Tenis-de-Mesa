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

import com.eduq.quatoca.torneiotmapi.api.assembler.PartidaAssembler;
import com.eduq.quatoca.torneiotmapi.api.assembler.PartidaResumoAssembler;
import com.eduq.quatoca.torneiotmapi.api.model.PartidaModel;
import com.eduq.quatoca.torneiotmapi.api.model.PartidaResumoModel;
import com.eduq.quatoca.torneiotmapi.domain.model.Partida;
import com.eduq.quatoca.torneiotmapi.domain.service.GestaoPartidaService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/partidas")
public class PartidaController {
	
	private PartidaAssembler partidaAssembler;
	private PartidaResumoAssembler partidaResumoAssembler;
	private GestaoPartidaService gestaoPartidaService;
	
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
	public PartidaModel adicionar(@PathVariable Long jogadorAId, @PathVariable Long jogadorBId) {
		return partidaAssembler.toModel(gestaoPartidaService.prepararPartida(jogadorAId, jogadorBId));
	}
	
	@PutMapping
	@RequestMapping("/iniciarPartida/{partidaId}")
	public Partida iniciarPartida(@PathVariable Long partidaId) {
		return gestaoPartidaService.iniciarPartida(partidaId);
	}

	@PutMapping
	@RequestMapping("/continuarPartida/{partidaId}")
	public Partida continuarPartida(@PathVariable Long partidaId) {
		return gestaoPartidaService.continuarPartida(partidaId);
	}
}
