package com.eduq.quatoca.torneiotmapi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.eduq.quatoca.torneiotmapi.model.Jogador;
import com.eduq.quatoca.torneiotmapi.repository.JogadorRepository;

@RestController
@RequestMapping("/jogadores")
public class JogadorController {
	
	@Autowired
	private JogadorRepository jogadorRepository;
	
	@GetMapping
	public List<Jogador> listar() {
		return jogadorRepository.findAll();
	}
	
	@GetMapping("/{jogadorId}")
	public ResponseEntity<Jogador> buscar(@PathVariable Long jogadorId) {
		return jogadorRepository.findById(jogadorId)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Jogador adicionar(@RequestBody Jogador jogador) {
		return jogadorRepository.save(jogador);
	}
	
	
}
