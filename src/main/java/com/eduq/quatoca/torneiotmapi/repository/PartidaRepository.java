package com.eduq.quatoca.torneiotmapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eduq.quatoca.torneiotmapi.model.Partida;

@Repository
public interface PartidaRepository extends JpaRepository<Partida, Long>{

}
