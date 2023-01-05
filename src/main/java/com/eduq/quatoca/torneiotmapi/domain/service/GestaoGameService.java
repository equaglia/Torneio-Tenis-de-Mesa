package com.eduq.quatoca.torneiotmapi.domain.service;

import com.eduq.quatoca.torneiotmapi.domain.model.Game;
import com.eduq.quatoca.torneiotmapi.domain.model.Jogador;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface GestaoGameService {
    Game buscar(Long gameId);

    List<Game> listar();

    @Transactional
    void salvar(Game game);

    @Transactional
    Game prepararGame(Optional<Jogador> jogadorA, Optional<Jogador> jogadorB, int numero);

    @Transactional
    void iniciarGame(Game game);

    void setEmAndamento(Game game);

    @Transactional
    void finalizarGame(Game game);

    boolean proximoGameProntoParaIniciar(Game game);

    boolean isGameEmAndamento(Game game);

    @Transactional
    void garantirGameAnteriorJaFinalizado(Game game);

    boolean isGamePar(Game game);

    boolean isGameImpar(Game game);

    int getTotalPontos(Game game);

    void excluir(Game game);
}
