package com.eduq.quatoca.torneiotmapi.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.eduq.quatoca.torneiotmapi.model.Game;
import com.eduq.quatoca.torneiotmapi.model.Jogador;
import com.eduq.quatoca.torneiotmapi.model.JogadoresPartidas;
import com.eduq.quatoca.torneiotmapi.model.Partida;
import com.eduq.quatoca.torneiotmapi.repository.GameRepository;
import com.eduq.quatoca.torneiotmapi.repository.JogadorRepository;
import com.eduq.quatoca.torneiotmapi.repository.JogadoresPartidasRepository;
import com.eduq.quatoca.torneiotmapi.repository.PartidaRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/partidas")
public class PartidaController {
	
//	@Autowired
	private PartidaRepository partidaRepository;
//	@Autowired
	private JogadorRepository jogadorRepository;
//	@Autowired
	private GameRepository gameRepository;
	private JogadoresPartidasRepository jogadoresPartidasRepository; 
	
	@GetMapping
	public List<Partida> listar() {
		return partidaRepository.findAll();	
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@RequestMapping("/{jogadorAId}/{jogadorBId}")
	public Partida adicionar(@PathVariable Long jogadorAId, @PathVariable Long jogadorBId) {
		Partida partida = new Partida();
		Jogador jogadorA = jogadorRepository.getById(jogadorAId);
		Jogador jogadorB = jogadorRepository.getById(jogadorBId);
		Set<Jogador> jogadores = new HashSet<Jogador>();
		JogadoresPartidas jogadoresDaPartida = new JogadoresPartidas();
		List<Game> games = new ArrayList<Game>();

		jogadores.add(jogadorA);
		jogadores.add(jogadorB);

		
		jogadoresDaPartida.setJogadorDireita(jogadorRepository.getById(jogadorAId));
		jogadoresDaPartida.setJogadorEsquerda(jogadorRepository.getById(jogadorBId));
		
		
		for (int i = 0; i < 5; i++) {
			games.add(gameRepository.save(new Game()));
		}
		partida.setGames(games);
//		partida.setJogadorDireita(jogadoresDaPartida);
		
		jogadoresDaPartida.setPartida(partida);
		jogadoresPartidasRepository.save(jogadoresDaPartida);
		return partidaRepository.save(partida);
	}
}
