package com.eduq.quatoca.torneiotmapi.domain.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eduq.quatoca.torneiotmapi.domain.exception.EntidadeNaoEncontradaException;
import com.eduq.quatoca.torneiotmapi.domain.exception.JogadorException;
import com.eduq.quatoca.torneiotmapi.domain.exception.NegocioException;
import com.eduq.quatoca.torneiotmapi.domain.model.Jogador;
import com.eduq.quatoca.torneiotmapi.domain.repository.JogadorRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class CatalogoJogadorService {

	private JogadorRepository jogadorRepository;
	
	public Optional<Jogador> buscar(Long jogadorId) {
		return Optional.of(jogadorRepository.findById(jogadorId)
				.orElseThrow(() -> new JogadorException("Jogador não encontrado CatalogoJogadorService")));
	}
	
	public List<Jogador> listar() {
		return jogadorRepository.findAll();
	}

	@Transactional
	public Jogador salvar(Jogador jogador) {
		// TODO TBD controles, tipo: email, fone
		return jogadorRepository.save(jogador);
	}

	@Transactional
	public void excluir(Long jogadorId) {
		Jogador jogadorParaExcluir = jogadorRepository.findById(jogadorId)
				.orElseThrow(() -> new EntidadeNaoEncontradaException("Jogador não encontrado"));

		if (jogadorParaExcluir.getPartidas().isEmpty()) {
			jogadorRepository.deleteById(jogadorId);
			new ResponseEntity<>("Jogador deletado do sistema", HttpStatus.NO_CONTENT);
		} else {
			throw(new NegocioException("Jogador com partida registrada não pode ser deletado."));
		}
	}
}
