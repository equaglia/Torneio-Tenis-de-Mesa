package com.eduq.quatoca.torneiotmapi.domain.service.impl;

import java.util.List;
import java.util.Optional;

import com.eduq.quatoca.torneiotmapi.domain.service.CatalogoJogadorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eduq.quatoca.torneiotmapi.domain.exception.EntidadeNaoEncontradaException;
import com.eduq.quatoca.torneiotmapi.domain.exception.NegocioException;
import com.eduq.quatoca.torneiotmapi.domain.model.Jogador;
import com.eduq.quatoca.torneiotmapi.domain.repository.JogadorRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class CatalogoJogadorServiceImpl implements CatalogoJogadorService {

	private JogadorRepository jogadorRepository;
	
	@Override
	public Optional<Jogador> buscar(Long jogadorId) {
		return Optional.of(jogadorRepository.findById(jogadorId)
				.orElseThrow(() -> new EntidadeNaoEncontradaException("Jogador não encontrado CatalogoJogadorService")));
	}
	
	@Override
	public List<Jogador> listar() {
		return jogadorRepository.findAll();
	}

	@Override
	@Transactional
	public Jogador salvar(Jogador jogador) {
		// TODO TBD controles, tipo: email, fone
		return jogadorRepository.save(jogador);
	}

	@Override
	@Transactional
	public void excluir(Long jogadorId) {
		Jogador jogadorParaExcluir = this.buscar(jogadorId).orElse(null);

		if (jogadorParaExcluir == null)
			throw (new NegocioException("Jogador não existente"));
		else if (jogadorParaExcluir.getPartidas().isEmpty()) {
			jogadorRepository.deleteById(jogadorId);
			new ResponseEntity<>("Jogador deletado do sistema", HttpStatus.NO_CONTENT);
		} else {
			throw(new NegocioException("Jogador com partida registrada não pode ser deletado."));
		}
	}
}
