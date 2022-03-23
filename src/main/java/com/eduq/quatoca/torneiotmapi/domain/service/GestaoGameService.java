package com.eduq.quatoca.torneiotmapi.domain.service;

import java.time.OffsetDateTime;
import java.util.Optional;

import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
	
//	Logger logger = LogManager.getLogger(GestaoGameService.class);

	private GameRepository gameRepository;
	private GestaoPontuacaoService gestaoPontuacaoService;

//	private Integer pontosParaVencer = 11;
//	public int getPontosParaVencer() {
//		return pontosParaVencer;
//	}
//	
//	public void setPontosParaVencer(int pontosParaVencer) {
//		this.pontosParaVencer = pontosParaVencer;
//	}
	
	

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
		System.out.println("entrou GestaoGameService:somaUmPonto");
		Game game = this.buscar(gameId);
		Boolean gameAtivo = false;
		if (gameSendoJogado(game)) {
//			logger.info("entrou if (gameSendoJogado(game)) { GestaoGameService:somaUmPonto");
			System.out.println("entrou if (gameSendoJogado(game)) { GestaoGameService:somaUmPonto");
			gameAtivo = true;
		} else if (proximoGameProntoParaIniciar(game)) {
			game.getPartida().gameAnterior().finalizar();
			this.salvar(game.getPartida().gameAnterior());
			game.iniciar();
			this.salvar(game);
			gameAtivo = true;
		}

		if (gameAtivo) {
			System.out.println("gameAtivo=true GestaoGameService:somaUmPonto");
			Pontuacao pontuacaoJogadorA = gestaoPontuacaoService.buscar(game.getPontos().get(0).getId());
			Pontuacao pontuacaoJogadorB = gestaoPontuacaoService.buscar(game.getPontos().get(1).getId());
			if (pontuacaoJogadorA.getId() == pontoId) {
				incrementar(pontuacaoJogadorA);
				finalizarGameComOnzePontosOuDiferencaMaiorQueDois(game, pontuacaoJogadorA, pontuacaoJogadorB);

			} else if (pontuacaoJogadorB.getId() == pontoId) {
				incrementar(pontuacaoJogadorB);
				finalizarGameComOnzePontosOuDiferencaMaiorQueDois(game, pontuacaoJogadorB, pontuacaoJogadorA);

			} else
				throw new EntidadeNaoEncontradaException("Game não encontrado");
		}
//		logger.info("saindo GestaoGameService:somaUmPonto");
		System.out.println("saindo GestaoGameService:somaUmPonto");
		return game;
	}

	@Transactional
	public void iniciarGame(Long gameId) {
		Game game = this.buscar(gameId);
		game.iniciar();
		this.salvar(game);		
	}
	
	private void finalizarGameComOnzePontosOuDiferencaMaiorQueDois(Game game, Pontuacao pontuacaoJogadorA, Pontuacao pontuacaoJogadorB) {
		int pontosParaVencer = 11;
		if (pontuacaoJogadorA.getPontos() >= pontosParaVencer
				&& Math.abs(pontuacaoJogadorA.getPontos() - pontuacaoJogadorB.getPontos()) >= 2) {
			game.finalizar();;
			this.salvar(game);
		}
	}

	private boolean proximoGameProntoParaIniciar(Game game) {
		return game.getPartida().gameAnterior().finalizado() && game.preparado() && game.getPartida().emAndamento();
	}

	private boolean gameSendoJogado(Game game) {
		System.out.println("entrou gameSendoJogado(game) GestaoGameService:somaUmPonto");
		return game.getPartida().buscarGameEmAndamento() == game;
	}

	private void incrementar(Pontuacao pontuacao) {
		pontuacao.setPontos(pontuacao.mais1ponto());
		gestaoPontuacaoService.salvar(pontuacao);
	}


//	private boolean gameEmCurso(Game game) {
//		if (game.preparado()) {
//			game.iniciar();
//			this.salvar(game);
//			return true;
//		} else if (game.emAndamento()) {
//			return true;
//		}
//		return false;
//	}

}
