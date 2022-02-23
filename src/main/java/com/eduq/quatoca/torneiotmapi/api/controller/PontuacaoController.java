package com.eduq.quatoca.torneiotmapi.api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eduq.quatoca.torneiotmapi.domain.model.Pontuacao;
import com.eduq.quatoca.torneiotmapi.domain.repository.GameRepository;
import com.eduq.quatoca.torneiotmapi.domain.repository.JogadorRepository;
import com.eduq.quatoca.torneiotmapi.domain.repository.PartidaRepository;
import com.eduq.quatoca.torneiotmapi.domain.repository.PontuacaoRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/pontos")
public class PontuacaoController {

	private PontuacaoRepository pontuacaoRepository; 
	private PartidaRepository partidaRepository;
	private JogadorRepository jogadorRepository;
	private GameRepository gameRepository;
	
	@GetMapping
	public List<Pontuacao> listar() {
		return pontuacaoRepository.findAll();	
	}


}
