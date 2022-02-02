package com.eduq.quatoca.torneiotmapi.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eduq.quatoca.torneiotmapi.model.JogadoresPartidas;
import com.eduq.quatoca.torneiotmapi.repository.GameRepository;
import com.eduq.quatoca.torneiotmapi.repository.JogadorRepository;
import com.eduq.quatoca.torneiotmapi.repository.JogadoresPartidasRepository;
import com.eduq.quatoca.torneiotmapi.repository.PartidaRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/jogadores_partidas")
public class JogadoresPartidasController {
	
	private JogadoresPartidasRepository jogadoresPartidasRepository;
	
	private PartidaRepository partidaRepository;
	private JogadorRepository jogadorRepository;

	public List<JogadoresPartidas> listar() {
		return jogadoresPartidasRepository.findAll();
		
	}

}
