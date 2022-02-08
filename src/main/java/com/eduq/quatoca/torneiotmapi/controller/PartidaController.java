package com.eduq.quatoca.torneiotmapi.controller;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
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
import com.eduq.quatoca.torneiotmapi.model.Partida;
import com.eduq.quatoca.torneiotmapi.model.Pontuacao;
import com.eduq.quatoca.torneiotmapi.repository.GameRepository;
import com.eduq.quatoca.torneiotmapi.repository.JogadorRepository;
import com.eduq.quatoca.torneiotmapi.repository.PartidaRepository;
import com.eduq.quatoca.torneiotmapi.repository.PontuacaoRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/partidas")
public class PartidaController {
	
//			game.setInicio(OffsetDateTime.now());
//			game.setFim(OffsetDateTime.now());
	private PartidaRepository partidaRepository;
	private JogadorRepository jogadorRepository;
	private GameRepository gameRepository;
	private PontuacaoRepository pontuacaoRepository;
	
	@GetMapping
	public List<Partida> listar() {
		return partidaRepository.findAll();	
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@RequestMapping("/{jogadorAId}/{jogadorBId}")
	public Partida adicionar(@PathVariable Long jogadorAId, @PathVariable Long jogadorBId) {
		String datesmall = "2022-02-03T16:05";
		Jogador jogadorA = jogadorRepository.getById(jogadorAId);
		Jogador jogadorB = jogadorRepository.getById(jogadorBId);

		Partida partida = new Partida();
		partida.setInicio(OffsetDateTime.of(LocalDateTime.parse(datesmall), ZoneOffset.UTC));
		partida.setFim(OffsetDateTime.of(LocalDateTime.parse(datesmall), ZoneOffset.UTC));
		List<Game> games = new ArrayList<Game>();
		for (int i = 0; i < 2; i++) {
			Game game = new Game();
			game.setInicio(OffsetDateTime.of(LocalDateTime.parse(datesmall), ZoneOffset.UTC));
			game.setFim(OffsetDateTime.of(LocalDateTime.parse(datesmall), ZoneOffset.UTC));
			
			Set<Pontuacao> pontos = new HashSet<Pontuacao>();

			Pontuacao ptA = new Pontuacao();
			ptA.setGame(game);
			ptA.setJogador(jogadorA);
			Pontuacao ptB = new Pontuacao();
			ptB.setGame(game);
			ptB.setJogador(jogadorB);
//			pontos.add(pontuacaoRepository.save(ptA));
//			pontos.add(pontuacaoRepository.save(ptB));
			pontos.add(ptA);
			pontos.add(ptB);
			game.setPontos(pontos);
//			Long gameid = game.getId();
//			gameRepository.save(game);
//			game.setId(gameid);
			games.add(game);
			gameRepository.save(game);
		}
		partida.setGames(games);

		List<Jogador> jogadores = new ArrayList<Jogador>();
		jogadores.add(jogadorA);
		jogadores.add(jogadorB);

		partida.setJogadores(jogadores);
		
		return partidaRepository.save(partida);
	}
}
