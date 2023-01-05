package com.eduq.quatoca.torneiotmapi.domain.service;

import com.eduq.quatoca.torneiotmapi.domain.model.Game;
import com.eduq.quatoca.torneiotmapi.domain.model.Pontuacao;

import javax.transaction.Transactional;

public interface PontuacaoEmGameService {
    @Transactional
    Game atualizarPontuacao(Long gameId, int pontuacaoA, int pontuacaoB);

    @Transactional
    Game atualizarPontuacaoGameFinalizado(Long gameId, int pontuacaoA, int pontuacaoB);

    @Transactional
    Game somaUmPonto(Long gameId, Long pontoId);

    @Transactional
    Game diminueUmPonto(Long gameId, Long pontoId);

    Pontuacao buscarPontuacaoDeJogador(Game game, int indice);
}
