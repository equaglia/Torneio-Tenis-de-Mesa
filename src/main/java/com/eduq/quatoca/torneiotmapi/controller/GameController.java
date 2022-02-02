package com.eduq.quatoca.torneiotmapi.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.eduq.quatoca.torneiotmapi.model.Game;
import com.eduq.quatoca.torneiotmapi.model.Jogador;
import com.eduq.quatoca.torneiotmapi.repository.GameRepository;

@RestController
@RequestMapping("/games")
public class GameController {

	@Autowired
	private GameRepository gameRepository;

	@GetMapping
	public List<Game> listar() {
		return gameRepository.findAll();
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Game adicionar() {
		
//		List<Game> gamesDaPartida = new ArrayList<>();
//		for (int i =0; i < 5; i++) {
//			Game game = new Game();
//			gamesDaPartida.add(game);
//		}
//		return gameRepository.save(gamesDaPartida);
		return gameRepository.save(new Game());
	}
}
