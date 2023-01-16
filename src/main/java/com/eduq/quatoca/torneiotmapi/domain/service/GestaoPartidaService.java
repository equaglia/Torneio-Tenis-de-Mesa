package com.eduq.quatoca.torneiotmapi.domain.service;

import com.eduq.quatoca.torneiotmapi.domain.model.Game;
import com.eduq.quatoca.torneiotmapi.domain.model.Partida;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.time.OffsetDateTime;
import java.util.List;

public interface GestaoPartidaService {
    Partida buscar(Long partidaId);

    List<Partida> listar();

    @Transactional
    Partida salvar(Partida partida);

    @Transactional
    Partida prepararPartida(Long jogadorAId, Long jogadorBId, int quantidadeGames);

    @Transactional
    Partida iniciarPartida(Long partidaId);

    @Transactional
    Partida continuarPartida(Long partidaId);

    @Transactional
    Partida interromperPartida(Long partidaId);

    Partida retornarPartidaInterrompida(Long partidaId);

    @Transactional
    void finalizarPartida(Partida partida);

    @Transactional
    Partida cancelarPartida(Long partidaId);

    @Transactional
    void excluirPartida(Long partidaId);

    @Transactional
    void setPartidaEmAndamento(Partida partida);

    void checaSeJogadoresSelecionadosCorretamente(Partida partida);

    boolean partidaJaTemVencedor(Partida partida);

    default int gamesParaVencerPartida(Partida partida) {
        return partida.getQuantidadeGames() / 2 + 1;
    }

    boolean temGameEmAndamento(Partida partida);

    @Transactional
    Partida completarPontuacaoEFinalizarPartida(Long partidaId, @Valid Partida partidaInput);

    default OffsetDateTime getFimValido(Game gameIn, OffsetDateTime fimIn) {
        if (!(gameIn.getFim() == null))
            fimIn = gameIn.getFim();
        return fimIn;
    }

    default OffsetDateTime getInicioValido(Game gameIn, OffsetDateTime inicioIn) {
        if (!(gameIn.getInicio() == null))
            inicioIn = gameIn.getInicio();
        return inicioIn;
    }

    void moverParaProximoGame(Partida partida);

    Game  proximoGame(Partida partida);
}
