package com.eduq.quatoca.torneiotmapi.domain.service;

import org.springframework.stereotype.Service;

import com.eduq.quatoca.torneiotmapi.domain.model.Jogador;
import com.eduq.quatoca.torneiotmapi.domain.repository.JogadorRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class SolicitacaoJogadorService {
	
	private CatalogoJogadorService catalogoJogadorService;
	private JogadorRepository jogadorRepository;
	

	public Jogador solicitarCadastro(Jogador jogador) {
		return jogadorRepository.save(jogador);
	}
}
