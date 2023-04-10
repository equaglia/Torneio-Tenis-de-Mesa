package com.eduq.quatoca.torneiotmapi.api.controller;

import java.util.List;

import jakarta.validation.Valid;

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

import com.eduq.quatoca.torneiotmapi.api.assembler.JogadorAssembler;
import com.eduq.quatoca.torneiotmapi.api.assembler.JogadorPartidasAssembler;
import com.eduq.quatoca.torneiotmapi.api.model.JogadorModel;
import com.eduq.quatoca.torneiotmapi.api.model.JogadorPartidasModel;
import com.eduq.quatoca.torneiotmapi.domain.model.Jogador;
import com.eduq.quatoca.torneiotmapi.domain.service.CatalogoJogadorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/jogadores")
@Tag(name = "JOGADORES", description = "Controle de informações de jogadores")
public class JogadorController {
	
	private JogadorAssembler jogadorAssembler;
	private JogadorPartidasAssembler jogadorPartidasAssembler;
	private CatalogoJogadorService catalogoJogadorService;
	
	
	@Operation(summary = "Criação de jogador",
			description = "Adicionar novo jogador à base de dados")
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Jogador adicionar(@Valid @RequestBody Jogador jogador) {
		return catalogoJogadorService.salvar(jogador);
	}

	@Operation(summary = "Atualização de jogador",
			description = "Atualizar as informaçoes do jogador")
	@PutMapping("/{jogadorId}")
	public ResponseEntity<Jogador> atualizar(
			@Parameter(description = "Identificador único do jogador no BD") @PathVariable Long jogadorId,
			@Valid @RequestBody Jogador jogador) {
		return catalogoJogadorService.buscar(jogadorId)
				.map(record -> {
					record.setCategoria(jogador.getCategoria());
					record.setNome(jogador.getNome());
					record.setSobrenome(jogador.getSobrenome());
					Jogador updated = catalogoJogadorService.salvar(record);
					return ResponseEntity.ok().body(updated);
				}).orElse(ResponseEntity.notFound().build());
	}
	
	@Operation(summary = "Lista dos jogadores",
			description = "Listar os jogadores da base de dados")
	@GetMapping
	public List<JogadorModel> listar() {
		return jogadorAssembler.toCollectionModel(catalogoJogadorService.listar());
	}
	
	@Operation(summary = "Informações do jogador",
			description = "Carregar as informações do jogador")
	@GetMapping("/{jogadorId}")
	public ResponseEntity<JogadorPartidasModel> buscar(
			@Parameter(description = "Identificador único do jogador no BD") @PathVariable Long jogadorId) {
		return catalogoJogadorService.buscar(jogadorId)
				.map(jogador -> ResponseEntity.ok(jogadorPartidasAssembler.toModel(jogador)))
				.orElse(ResponseEntity.notFound().build());
	}
	
	@Operation(summary = "Remoção de jogador",
			description = "Remover jogador da base de dados. Para ser removido, o jogador não deve estar alocado a nenhuma partida, qualquer que seja o status da partida.")
	@DeleteMapping("/{jogadorId}")
	public ResponseEntity<Void> excluir(
			@Parameter(description = "Identificador único do jogador no BD") @PathVariable Long jogadorId) {
		catalogoJogadorService.excluir(jogadorId);
		return ResponseEntity.noContent().build();
	}
	
}
