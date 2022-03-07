package com.eduq.quatoca.torneiotmapi.domain.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eduq.quatoca.torneiotmapi.domain.exception.JogoException;
import com.eduq.quatoca.torneiotmapi.domain.model.Jogador;
import com.eduq.quatoca.torneiotmapi.domain.repository.JogadorRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class CatalogoJogadorService {

	private JogadorRepository jogadorRepository;
	
	@Transactional
	public Jogador buscar(Long jogadorId) {
		return jogadorRepository.findById(jogadorId)
				.orElseThrow(() -> new JogoException("Jogador não encontrado"));
	}

	@Transactional
	public Jogador salvar(Jogador jogador) {
		// TODO TBD controles, tipo: email, fone
		return jogadorRepository.save(jogador);
	}

	@Transactional
	public void excluir(Long jogadorId) {
		Jogador jogadorParaExcluir = jogadorRepository.findById(jogadorId).get();
		if (jogadorParaExcluir.getPartidas().isEmpty()) {
			jogadorRepository.deleteById(jogadorId);
			System.out.println("Jogador deletado do sistema");
		} else {
			throw(new JogoException("Jogador com partida registrada não pode ser deletado."));
		}
	}
}
