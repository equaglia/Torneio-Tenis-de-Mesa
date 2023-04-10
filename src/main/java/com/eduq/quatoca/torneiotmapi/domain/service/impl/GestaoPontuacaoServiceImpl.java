package com.eduq.quatoca.torneiotmapi.domain.service.impl;

import jakarta.transaction.Transactional;

import com.eduq.quatoca.torneiotmapi.domain.service.GestaoPontuacaoService;
import org.springframework.stereotype.Service;

import com.eduq.quatoca.torneiotmapi.domain.exception.EntidadeNaoEncontradaException;
import com.eduq.quatoca.torneiotmapi.domain.model.Pontuacao;
import com.eduq.quatoca.torneiotmapi.domain.repository.PontuacaoRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class GestaoPontuacaoServiceImpl implements GestaoPontuacaoService {
	
	private PontuacaoRepository pontuacaoRepository;

//	public Optional<Pontuacao> buscar(Long pontuacaoId) { 
	@Override public Pontuacao buscar(Long pontuacaoId) { 
		return pontuacaoRepository.findById(pontuacaoId)
				.orElseThrow(() -> new EntidadeNaoEncontradaException("Pontuacao não encontrada GestaoPontuacaoService"));
	}

	@Override@Transactional
	public void salvar(Pontuacao pontuacao) {
		pontuacaoRepository.save(pontuacao);
	}

	@Override@Transactional
	public Pontuacao preparaPontuacao() {
		Pontuacao pontuacao = new Pontuacao();
		pontuacao.setPontos(0);
		this.salvar(pontuacao);
		return pontuacao;
	}

	@Override public void excluir(Pontuacao pontuacao) {
		pontuacaoRepository.delete(pontuacao);
		
	}
}
