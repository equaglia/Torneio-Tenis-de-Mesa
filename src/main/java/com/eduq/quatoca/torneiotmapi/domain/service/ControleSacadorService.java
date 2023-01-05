package com.eduq.quatoca.torneiotmapi.domain.service;

import com.eduq.quatoca.torneiotmapi.domain.model.Jogador;

import javax.transaction.Transactional;

public interface ControleSacadorService {
    @Transactional
    void setPrimeiroSacador(Long partidaId, Long jogadorId);

    Jogador getSacador(Long partidaId, Long gameId);
}
