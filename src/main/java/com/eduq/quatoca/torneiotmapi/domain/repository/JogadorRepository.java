package com.eduq.quatoca.torneiotmapi.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eduq.quatoca.torneiotmapi.domain.model.Jogador;

@Repository
public interface JogadorRepository extends JpaRepository<Jogador, Long>{
	
	List<Jogador> findByNome(String nome);
	List<Jogador> findByNomeContaining(String nome);
//	Optional<Jogador> findByEmail(String email);

}
