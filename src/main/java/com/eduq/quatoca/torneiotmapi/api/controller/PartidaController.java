package com.eduq.quatoca.torneiotmapi.api.controller;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.eduq.quatoca.torneiotmapi.api.assembler.PartidaAssembler;
import com.eduq.quatoca.torneiotmapi.api.model.PartidaModel;
import com.eduq.quatoca.torneiotmapi.domain.model.Game;
import com.eduq.quatoca.torneiotmapi.domain.model.Jogador;
import com.eduq.quatoca.torneiotmapi.domain.model.Partida;
import com.eduq.quatoca.torneiotmapi.domain.model.Pontuacao;
import com.eduq.quatoca.torneiotmapi.domain.repository.GameRepository;
import com.eduq.quatoca.torneiotmapi.domain.repository.JogadorRepository;
import com.eduq.quatoca.torneiotmapi.domain.repository.PartidaRepository;
import com.eduq.quatoca.torneiotmapi.domain.repository.PontuacaoRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/partidas")
public class PartidaController {
	
	private PartidaRepository partidaRepository;
	private JogadorRepository jogadorRepository;
	private GameRepository gameRepository;
	private PontuacaoRepository pontuacaoRepository;
	private PartidaAssembler partidaAssembler;
	
	@GetMapping
	public List<PartidaModel> listar() {
		return partidaAssembler.toCollectionModel(partidaRepository.findAll());
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@RequestMapping("/{jogadorAId}/{jogadorBId}")
	public PartidaModel adicionar(@PathVariable Long jogadorAId, @PathVariable Long jogadorBId) {
		String datesmall = "2022-02-03T16:05";
		Partida partida = new Partida();

		Optional<Jogador> jogadorA = jogadorRepository.findById(jogadorAId);
		Optional<Jogador> jogadorB = jogadorRepository.findById(jogadorBId);
		partida.addJogador(jogadorA.get());
		partida.addJogador(jogadorB.get());

		partida.setInicio(OffsetDateTime.of(LocalDateTime.parse(datesmall), ZoneOffset.UTC));
		partida.setFim(OffsetDateTime.of(LocalDateTime.parse(datesmall), ZoneOffset.UTC));
		for (int i = 0; i < 2; i++) {
			Game game = new Game();
			game.setInicio(OffsetDateTime.of(LocalDateTime.parse(datesmall), ZoneOffset.UTC));
			game.setFim(OffsetDateTime.of(LocalDateTime.parse(datesmall), ZoneOffset.UTC));

			Pontuacao ptA = new Pontuacao();
			ptA.setPontos(0);
			pontuacaoRepository.save(ptA);
			game.addPontuacao(ptA, jogadorA.get());
			
			Pontuacao ptB = new Pontuacao();
			ptB.setPontos(0);
			pontuacaoRepository.save(ptB);
			game.addPontuacao(ptB, jogadorB.get());
			gameRepository.save(game);
			partida.addGame(game);
		}

		jogadorRepository.save(jogadorA.get());
		jogadorRepository.save(jogadorB.get());
		partidaRepository.save(partida);
		return partidaAssembler.toModel(partida);
	}

}
