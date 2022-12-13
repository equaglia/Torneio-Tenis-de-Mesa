package com.eduq.quatoca.torneiotmapi.domain.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.eduq.quatoca.torneiotmapi.domain.exception.EntidadeNaoEncontradaException;
import com.eduq.quatoca.torneiotmapi.domain.model.Game;
import com.eduq.quatoca.torneiotmapi.domain.model.Jogador;
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

	public List<Game> listar() {
		return gameRepository.findAll();
	}

	@Transactional
	public void salvar(Game game) {gameRepository.save(game);
	}

	@Transactional
	public Game prepararGame(Optional<Jogador> jogadorA, Optional<Jogador> jogadorB, int numero) {
		Game game = new Game();
		game.addPontuacao(gestaoPontuacaoService.preparaPontuacao(), jogadorA.orElse(null));
		game.addPontuacao(gestaoPontuacaoService.preparaPontuacao(), jogadorB.orElse(null));
		game.setNumero(numero);
		this.salvar(game);
		return game;
	}

	@Transactional
	public void iniciarGame(Long gameId) {
		Game game = this.buscar(gameId);
		iniciarGame(game);
	}

	@Transactional
	public void iniciarGame(Game game) {
		game.iniciar();
		this.salvar(game);
	}

	public void setEmAndamento(Game game) {
		game.setEmAndamento();
		game.setFim(null);
		this.salvar(game);
	}

	@Transactional
	public void finalizarGame(Game game) {
		game.finalizar();
		this.salvar(game);
	}

	public boolean proximoGameProntoParaIniciar(Game game) {
//		return game.getPartida().gameAnterior().finalizado()
		if (game.getPartida().getGameAtualIndice() == 0)
			return game.preparado() && game.getPartida().emAndamento();
		return game.getPartida().getGame(game.getPartida().getGameAtualIndice()-1).finalizado()
				&& game.preparado()
				&& game.getPartida().emAndamento();
	}

	public boolean isGameEmAndamento(Game game) {

		return game.getPartida().buscarGameEmAndamento() == game;
	}

	@Transactional 
	void garantirGameAnteriorJaFinalizado(Game game) {
		game.getPartida().gameAnterior().finalizar();
		salvar(game.getPartida().gameAnterior());
	}
	
	public boolean isGamePar(Game game) {
		int gameIndice = game.getPartida().getGames().indexOf(game);
		return gameIndice % 2 == 0;
	}
	
	public boolean isGameImpar(Game game) {
		return !isGamePar(game);
	}
	
	public int getTotalPontos(Game game) {
		return game.getPontos().get(0).getPontos() 
				+ game.getPontos().get(1).getPontos();
	}

	public void excluir(Game game) {gameRepository.delete(game);
	}
}
