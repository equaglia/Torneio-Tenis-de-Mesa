package com.eduq.quatoca.torneiotmapi.domain.service;

import java.time.OffsetDateTime;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.eduq.quatoca.torneiotmapi.domain.exception.EntidadeNaoEncontradaException;
import com.eduq.quatoca.torneiotmapi.domain.model.Game;
import com.eduq.quatoca.torneiotmapi.domain.model.Jogador;
import com.eduq.quatoca.torneiotmapi.domain.model.Pontuacao;
import com.eduq.quatoca.torneiotmapi.domain.repository.GameRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class GestaoGameService {

	private GameRepository gameRepository;
	private GestaoPontuacaoService gestaoPontuacaoService;

	public Game buscar(Long gameId) { 
		return gameRepository.findById(gameId)
				.orElseThrow(() -> new EntidadeNaoEncontradaException("Game não encontrado GestaoGameService"));
	}
	
	@Transactional
	public Game salvar(Game game) {
		return gameRepository.save(game);
	}

	@Transactional
	public Game prepararGame(Optional<Jogador> jogadorA, Optional<Jogador> jogadorB, OffsetDateTime horarioInicial) {
		Game game = this.prepararGame(jogadorA, jogadorB);
		game.setInicio(horarioInicial);
		this.salvar(game);
		return game;
	}
	
	
	@Transactional
	public Game prepararGame(Optional<Jogador> jogadorA, Optional<Jogador> jogadorB) {
//		String datesmall = "2022-02-03T16:05"; // TBD provisório
		Game game = new Game();
		game.addPontuacao(gestaoPontuacaoService.preparaPontuacao(), jogadorA.get());
		game.addPontuacao(gestaoPontuacaoService.preparaPontuacao(), jogadorB.get());
		this.salvar(game);
		return game;
	}
	
	@Transactional
	public Game somaUmPonto(Long gameId, Long pontoId) {
		Game game = this.buscar(gameId);
		Pontuacao pontuacaoJogadorA = gestaoPontuacaoService.buscar(game.getPontos().get(0).getId());
		Pontuacao pontuacaoJogadorB = gestaoPontuacaoService.buscar(game.getPontos().get(1).getId());
		if (pontuacaoJogadorA.getId() == pontoId) {
			pontuacaoJogadorA.setPontos(pontuacaoJogadorA.mais1ponto());
			gestaoPontuacaoService.salvar(pontuacaoJogadorA);
		
		} else if (pontuacaoJogadorB.getId() == pontoId) {
			pontuacaoJogadorB.setPontos(pontuacaoJogadorB.mais1ponto());
			gestaoPontuacaoService.salvar(pontuacaoJogadorB);
			
		} else throw new EntidadeNaoEncontradaException("Game não encontrado");
		return game;
	}
}




















