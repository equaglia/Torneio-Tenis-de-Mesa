package com.eduq.quatoca.torneiotmapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eduq.quatoca.torneiotmapi.model.Game;

@Repository
public interface GameRepository extends JpaRepository<Game, Long>{

}
