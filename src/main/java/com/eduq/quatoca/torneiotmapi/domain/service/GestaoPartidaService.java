package com.eduq.quatoca.torneiotmapi.domain.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;

import com.eduq.quatoca.torneiotmapi.TmapiConfig;
import com.eduq.quatoca.torneiotmapi.common.CalculosGlobais;
import com.eduq.quatoca.torneiotmapi.domain.exception.EntidadeNaoEncontradaException;
import com.eduq.quatoca.torneiotmapi.domain.exception.NegocioException;
import com.eduq.quatoca.torneiotmapi.domain.model.Game;
import com.eduq.quatoca.torneiotmapi.domain.model.Jogador;
import com.eduq.quatoca.torneiotmapi.domain.model.Partida;
import com.eduq.quatoca.torneiotmapi.domain.repository.PartidaRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
@Import(TmapiConfig.class)
public class GestaoPartidaService {

	private CatalogoJogadorService catalogoJogadorService;
	private PartidaRepository partidaRepository;
	private GestaoGameService gestaoGameService;
	private GestaoPontuacaoService gestaoPontuacaoService;

	private static final int jogadorA = 0;
	private static final int jogadorB = 1;

	public Partida buscar(Long partidaId) {
		return partidaRepository.findById(partidaId)
				.orElseThrow(() -> new EntidadeNaoEncontradaException("Partida não encontrada GestaoPartidaService"));
	}

	public List<Partida> listar() {
		return partidaRepository.findAll();
	}

	@Transactional
	public Partida salvar(Partida partida) {
		return partidaRepository.save(partida);
	}

	@Transactional
	public Partida prepararPartida(Long jogadorAId, Long jogadorBId, int quantidadeGames) {

		Partida partida = new Partida();

		partida.setQuantidadeGames(quantidadeGames);

		Optional<Jogador> jogadorA = catalogoJogadorService.buscar(jogadorAId);
		Optional<Jogador> jogadorB = catalogoJogadorService.buscar(jogadorBId);
		partida.addJogador(jogadorA.orElse(null));
		partida.addJogador(jogadorB.orElse(null));

		checaSeJogadoresSelecionadosCorretamente(partida);

		for (int i = 0; i < quantidadeGames; i++) {
			partida.addGame(gestaoGameService.prepararGame(jogadorA, jogadorB, i));
		}
		System.out.println(partida);

		partida.setGameAtualIndice(0);

		catalogoJogadorService.salvar(jogadorA.orElse(null));
		catalogoJogadorService.salvar(jogadorB.orElse(null));
		this.salvar(partida);

		return partida;
	}

	@Transactional
	public Partida iniciarPartida(Long partidaId) {
		Partida partida = this.buscar(partidaId);
		//System.out.println("GestaoPartidaService.iniciarPartida antes de partida.iniciar(): game[0].id = "+partida.getGames().get(0).getId());
		partida.iniciar();
		//System.out.println("GestaoPartidaService.iniciarPartida antes de gestaoGameService.iniciarGame(partida.primeiroGameDaPartida()): game[0].id = "+partida.getGames().get(0).getId());
		gestaoGameService.iniciarGame(partida.primeiroGameDaPartida());
		//System.out.println("GestaoPartidaService.iniciarPartida antes de partida.setGameAtualIndice(0): game[0].id = "+partida.getGames().get(0).getId());
		partida.setGameAtualIndice(0);
		//System.out.println("GestaoPartidaService.iniciarPartida antes de this.salvar(partida): game[0].id = "+partida.getGames().get(0).getId());
		this.salvar(partida);
		//System.out.println("GestaoPartidaService.iniciarPartida DEPOIS de this.salvar(partida): game[0].id = "+partida.getGames().get(0).getId());
		return partida;
	}

	@Transactional
	public Partida continuarPartida(Long partidaId) {
		Partida partida = this.buscar(partidaId);
		if (partida.finalizado()) {
			return partida;
		} else {
			if (partidaJaTemVencedor(partida)) {
				finalizarPartida(partida);
			} else {
				Game gameEmJogo = null;
				if (partida.getGame(partida.getGameAtualIndice()) != null) {
					gameEmJogo = partida.getGame(partida.getGameAtualIndice());
				}
				if (partida.emAndamento()) {

					if (gameEmJogo == null) {
						finalizarPartida(partida);
					} else {
						gestaoGameService.iniciarGame(gameEmJogo);
					}

				} else {
					throw new NegocioException("Partida ainda precisa ser iniciada");
				}
			}
		}
		return this.salvar(partida);
	}

	@Transactional
	public void finalizarPartida(Partida partida) {
		partida.finalizar();
		this.salvar(partida);
	}

	@Transactional
	public Partida cancelarPartida(Long partidaId) {
		Partida partida = this.buscar(partidaId);
		if (partida.cancelado())
			throw new NegocioException("Partida com id:" + partida.getId() + " já estava cancelada");
		if (partida.emAndamento() || partida.interrompido()) {
			partida.liberarJogadores();
		}
		partida.cancelar();
		return partida;
	}

	@Transactional
	public void excluirPartida(Long partidaId) {
		Partida partida = this.buscar(partidaId);
		if (partida.cancelado()) {
			partida.getGames().forEach(game -> {
				game.getPontos().forEach(pontuacao -> gestaoPontuacaoService.excluir(pontuacao));
				game.setPontos(null);
				gestaoGameService.excluir(game);
			});
			partida.setGames(null);
			partida.setJogadores(null);
			partida.setPrimeiroSacador(null);
//			partida.setGameAtual(null);
			partida.setGameAtualIndice(-1);
			partidaRepository.delete(partida);
			System.out.println("partida "+partidaId+" deletada");

		} else
			throw new NegocioException("Somente partida CANCELADA pode ser excluída");
	}

	@Transactional
	public void setPartidaEmAndamento(Partida partida) {
		partida.setEmAndamento();
		System.out.println("partida "+partida.getId()+" entrou em andamento");

		this.salvar(partida);
	}

	public void checaSeJogadoresSelecionadosCorretamente(Partida partida) {
		switch (partida.getJogadores().size()) {
		case 0:
			throw new NegocioException("Nenhum jogador foi selecionado para a partida");
		case 1:
			Jogador jogadorSelecionado = new Jogador();
			for (Jogador jogador : partida.getJogadores())
				jogadorSelecionado = jogador;
			throw new NegocioException(
					"Somente o jogador " + jogadorSelecionado.getNome() + " foi selecionado para a partida");
		case 2: // Jogadores selecionados corretamente
			break;
		default:
			throw new NegocioException("Mais de 2 jogadores foram selecionados para a partida");
		}
	}

	public boolean partidaJaTemVencedor(Partida partida) {
		List<Integer> resultado = partida.calculaResultado();
		return resultado.get(jogadorA) == gamesParaVencerPartida(partida)
				|| resultado.get(jogadorB) == gamesParaVencerPartida(partida);
	}

	private int gamesParaVencerPartida(Partida partida) {
		return partida.getQuantidadeGames() / 2 + 1;
	}

	public boolean temGameEmAndamento(Partida partida) {
		Game game;
		for (Game value : partida.getGames()) {
			game = value;
			if (game.emAndamento())
				return true;
		}
		return false;
	}

	@Transactional
	public Partida completarPontuacaoEFinalizarPartida(Long partidaId, @Valid Partida partidaInput) {
		Partida partidaOut = this.buscar(partidaId);
		Game gameOut;
		Game gameIn;
		OffsetDateTime inicioIn = OffsetDateTime.now();
		OffsetDateTime fimIn = OffsetDateTime.now();
		List<Game> gamesIn = partidaInput.getGames();
		for (int i = 0; i < partidaOut.getGames().size(); i++) {
			if (i == 0) {
				partidaOut.iniciar();
				partidaOut.setInicio(getInicioValido(gamesIn.get(0), inicioIn));
			}
			gameOut = gestaoGameService.buscar(partidaOut.getGame(i).getId());
			if (gamesIn.size() > i) {
				gameIn = gamesIn.get(i);
				int pontosJogadorA = gameIn.getPontosJogador(jogadorA);
				int pontosJogadorB = gameIn.getPontosJogador(jogadorB);
				gameOut.setPontosJogador(jogadorA, pontosJogadorA);
				gameOut.setPontosJogador(jogadorB, pontosJogadorB);
				if (CalculosGlobais.pontuacaoParaFinalizarGame(pontosJogadorA, pontosJogadorB)) {
					gameOut.finalizar();
					inicioIn = getInicioValido(gameIn, inicioIn);
					fimIn = getFimValido(gameIn, fimIn);
					if (inicioIn.isBefore(fimIn)) {
						gameOut.setInicio(inicioIn);
						gameOut.setFim(fimIn);
					} else
						throw new NegocioException("Data início após data final");
					gestaoGameService.salvar(gameOut);
				} else
					throw new NegocioException("Pontuacao não finaliza o game");
			} else
				break;
		}

		if (partidaJaTemVencedor(partidaOut)) {
			this.finalizarPartida(partidaOut);
			this.salvar(partidaOut);
		} else {
			throw new NegocioException("Pontuacao não finaliza o game");
		}
		return partidaOut;
	}

	private OffsetDateTime getFimValido(Game gameIn, OffsetDateTime fimIn) {
		if (!(gameIn.getFim() == null))
			fimIn = gameIn.getFim();
		return fimIn;
	}

	private OffsetDateTime getInicioValido(Game gameIn, OffsetDateTime inicioIn) {
		if (!(gameIn.getInicio() == null))
			inicioIn = gameIn.getInicio();
		return inicioIn;
	}

	public void moverParaProximoGame(Partida partida) {
		partida.moverParaProximoGame();
		this.salvar(partida);
		if (this.partidaJaTemVencedor(partida)) {
			this.finalizarPartida(partida);
		}
	}
}
