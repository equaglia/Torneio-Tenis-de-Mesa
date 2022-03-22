package com.eduq.quatoca.torneiotmapi.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eduq.quatoca.torneiotmapi.domain.model.Pontuacao;

@Repository
public interface PontuacaoRepository extends JpaRepository<Pontuacao, Long>{

}
