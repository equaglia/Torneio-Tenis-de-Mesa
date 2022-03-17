package com.eduq.quatoca.torneiotmapi.api.controller;

import java.util.List;

import javax.validation.Valid;

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
import com.eduq.quatoca.torneiotmapi.domain.repository.JogadorRepository;
import com.eduq.quatoca.torneiotmapi.domain.service.CatalogoJogadorService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/jogadores")
public class JogadorController {
	
	private JogadorRepository jogadorRepository;
	private JogadorAssembler jogadorAssembler;
	private JogadorPartidasAssembler jogadorPartidasAssembler;
	private CatalogoJogadorService catalogoJogadorService;
	
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Jogador adicionar(@Valid @RequestBody Jogador jogador) {
		return catalogoJogadorService.salvar(jogador);
	}

	@PutMapping("/{jogadorId}")
	public ResponseEntity<Jogador> atualizar(
			@PathVariable Long jogadorId,
			@Valid @RequestBody Jogador jogador) {
		return jogadorRepository.findById(jogadorId)
				.map(record -> {
					record.setCategoria(jogador.getCategoria());
					record.setNome(jogador.getNome());
					record.setSobrenome(jogador.getSobrenome());
					Jogador updated = catalogoJogadorService.salvar(record);
					return ResponseEntity.ok().body(updated);
				}).orElse(ResponseEntity.notFound().build());
	}
	
	@GetMapping
	public List<JogadorModel> listar() {
		return jogadorAssembler.toCollectionModel(jogadorRepository.findAll());
	}
	
	@GetMapping("/{jogadorId}")
	public ResponseEntity<JogadorPartidasModel> buscar(@PathVariable Long jogadorId) {
		return jogadorRepository.findById(jogadorId)
				.map(jogador -> ResponseEntity.ok(jogadorPartidasAssembler.toModel(jogador)))
				.orElse(ResponseEntity.notFound().build());
//				.orElseThrow(() -> new JogadorException("Jogador não encontrado JogadorController"));
//				.orElse(ResponseEntity.badRequest().body(null));
	}
	
	@DeleteMapping("/{jogadorId}")
	public ResponseEntity<Void> excluir(@PathVariable Long jogadorId) {
		catalogoJogadorService.excluir(jogadorId);
		return ResponseEntity.noContent().build();
	}
	
}
