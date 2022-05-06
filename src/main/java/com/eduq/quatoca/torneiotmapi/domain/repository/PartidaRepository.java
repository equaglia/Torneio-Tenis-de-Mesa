package com.eduq.quatoca.torneiotmapi.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eduq.quatoca.torneiotmapi.domain.model.Partida;

@Repository
public interface PartidaRepository extends JpaRepository<Partida, Long>{
//	void cancelar(Partida partida); TODO como implementar método por aqui?

}
