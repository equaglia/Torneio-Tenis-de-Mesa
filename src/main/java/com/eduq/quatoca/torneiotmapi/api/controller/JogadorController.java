package com.eduq.quatoca.torneiotmapi.api.controller;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

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

import com.eduq.quatoca.torneiotmapi.api.dto.JogadorDTO;
// import com.eduq.quatoca.torneiotmapi.api.dto.assembler.JogadorAssembler;
import com.eduq.quatoca.torneiotmapi.api.dto.assembler.JogadorPartidasAssembler;
import com.eduq.quatoca.torneiotmapi.api.model.JogadorModel;
import com.eduq.quatoca.torneiotmapi.api.model.JogadorPartidasModel;
import com.eduq.quatoca.torneiotmapi.api.model.input.JogadorInput;
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
	
	private CatalogoJogadorService catalogoJogadorService;
	// private JogadorAssembler jogadorAssembler;


	private JogadorPartidasAssembler jogadorPartidasAssembler;
	
	@Operation(summary = "Criação de jogador",
			description = "Adicionar novo jogador à base de dados")
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public JogadorModel adicionar(@Valid @RequestBody JogadorModel jogadorDTO) {
	// public JogadorDTO adicionar(@Valid @RequestBody JogadorDTO jogadorDTO) {
	// public Jogador adicionar(@Valid @RequestBody Jogador jogador) {
		System.out.println("JogadorController.adicionar: "+jogadorDTO.toString());
		return catalogoJogadorService.salvar(jogadorDTO);
		// return jogadorAssembler.toEntity(jogador);
	}

	@Operation(summary = "Atualização de jogador",
			description = "Atualizar as informaçoes do jogador")
	@PutMapping("/{jogadorId}")
	public JogadorModel atualizar(
	// public JogadorDTO atualizar(
	// public ResponseEntity<JogadorDTO> atualizar(
			@Parameter(description = "Identificador único do jogador no BD") @PathVariable @NotNull Long jogadorId,
			@Valid @RequestBody JogadorModel jogadorDTO) {
			// @Valid @RequestBody JogadorDTO jogadorDTO) {
			return catalogoJogadorService.atualizar(jogadorId, jogadorDTO);

		// return catalogoJogadorService.buscar(jogadorId)
		// 		.map(record -> {
		// 			record.setCategoria(jogadorDTO.categoria());
		// 			record.setNome(jogadorDTO.nome());
		// 			record.setSobrenome(jogadorDTO.sobrenome());
		// 			JogadorDTO updated = catalogoJogadorService.salvar(record);
		// 			return ResponseEntity.ok().body(updated);

		// 			return
		// 		}).orElse(ResponseEntity.notFound().build());
	}
	
	@Operation(summary = "Lista dos jogadores",
			description = "Listar os jogadores da base de dados")
	@GetMapping
	public List<JogadorModel> listar() {
	// public List<JogadorDTO> listar() {
		return catalogoJogadorService.listar();
		// return jogadorAssembler.toCollectionModel(catalogoJogadorService.listar());
	}
	
	@Operation(summary = "Informações do jogador",
			description = "Carregar as informações do jogador")
	@GetMapping("/{jogadorId}")
	public ResponseEntity<JogadorPartidasModel> buscar(
			@Parameter(description = "Identificador único do jogador no BD") @PathVariable Long jogadorId) {
				return null; // TODO arrumar este método
		// return catalogoJogadorService.buscar(jogadorId)
		// 		.map(jogador -> ResponseEntity.ok(jogadorPartidasAssembler.toModel(jogador)))
		// 		.orElse(ResponseEntity.notFound().build());
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
