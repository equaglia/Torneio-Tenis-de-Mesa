package com.eduq.quatoca.torneiotmapi.domain.service;

import com.eduq.quatoca.torneiotmapi.domain.model.Jogador;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CatalogoJogadorService {
    Optional<Jogador> buscar(Long jogadorId);

    List<Jogador> listar();

    @Transactional
    Jogador salvar(Jogador jogador);

    @Transactional
    void excluir(Long jogadorId);
}
