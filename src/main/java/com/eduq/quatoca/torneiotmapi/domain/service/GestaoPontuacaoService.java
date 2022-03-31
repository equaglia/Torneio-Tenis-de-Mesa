package com.eduq.quatoca.torneiotmapi.domain.service;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.eduq.quatoca.torneiotmapi.domain.exception.EntidadeNaoEncontradaException;
import com.eduq.quatoca.torneiotmapi.domain.model.Pontuacao;
import com.eduq.quatoca.torneiotmapi.domain.repository.PontuacaoRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class GestaoPontuacaoService {
	
	private PontuacaoRepository pontuacaoRepository;

//	public Optional<Pontuacao> buscar(Long pontuacaoId) { 
	public Pontuacao buscar(Long pontuacaoId) { 
		return pontuacaoRepository.findById(pontuacaoId)
				.orElseThrow(() -> new EntidadeNaoEncontradaException("Pontuacao não encontrada GestaoPontuacaoService"));
	}

	@Transactional
	public Pontuacao salvar(Pontuacao pontuacao) {
		return pontuacaoRepository.save(pontuacao);
	}

	@Transactional
	public Pontuacao preparaPontuacao() {
		Pontuacao pontuacao = new Pontuacao();
		pontuacao.setPontos(0);
		this.salvar(pontuacao);
		return pontuacao;
	}
}
