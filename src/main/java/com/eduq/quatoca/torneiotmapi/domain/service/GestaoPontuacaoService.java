package com.eduq.quatoca.torneiotmapi.domain.service;

import com.eduq.quatoca.torneiotmapi.domain.model.Pontuacao;

import javax.transaction.Transactional;

public interface GestaoPontuacaoService {
    //	public Optional<Pontuacao> buscar(Long pontuacaoId) {
    Pontuacao buscar(Long pontuacaoId);

    @Transactional
    void salvar(Pontuacao pontuacao);

    @Transactional
    Pontuacao preparaPontuacao();

    void excluir(Pontuacao pontuacao);
}
