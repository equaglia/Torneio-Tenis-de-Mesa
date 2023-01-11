package com.eduq.quatoca.torneiotmapi.domain.service.impl;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;
import javax.validation.Valid;

import com.eduq.quatoca.torneiotmapi.domain.service.CatalogoJogadorService;
import com.eduq.quatoca.torneiotmapi.domain.service.GestaoGameService;
import com.eduq.quatoca.torneiotmapi.domain.service.GestaoPartidaService;
import com.eduq.quatoca.torneiotmapi.domain.service.GestaoPontuacaoService;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Sort;
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
public class GestaoPartidaServiceImpl implements GestaoPartidaService {

	private CatalogoJogadorService catalogoJogadorService;
	private PartidaRepository partidaRepository;
	private GestaoGameService gestaoGameServiceImpl;
	private GestaoPontuacaoService gestaoPontuacaoService;

	private static final int JOGADOR_A = 0;
	private static final int JOGADOR_B = 1;

	@Override
	public Partida buscar(Long partidaId) {
		return partidaRepository.findById(partidaId)
				.orElseThrow(() -> new EntidadeNaoEncontradaException("Partida não encontrada GestaoPartidaService"));
	}

	@Override
	public List<Partida> listar() {
		return partidaRepository.findAll(Sort.by("id"));
	}

	@Override
	@Transactional
	public Partida salvar(Partida partida) {
		return partidaRepository.save(partida);
	}

	@Override
	@Transactional
	public Partida prepararPartida(Long jogadorAId, Long jogadorBId, int quantidadeGames) {

		Partida partida = new Partida();

		partida.setQuantidadeGames(quantidadeGames);

		Optional<Jogador> jogadorA = catalogoJogadorService.buscar(jogadorAId);
		Optional<Jogador> jogadorB = catalogoJogadorService.buscar(jogadorBId);
		partida.setAdversarios(jogadorA.orElse(null), jogadorB.orElse(null));

		checaSeJogadoresSelecionadosCorretamente(partida);

		for (int i = 0; i < quantidadeGames; i++) {
			partida.addGame(gestaoGameServiceImpl.prepararGame(jogadorA, jogadorB, i));
		}
		System.out.println(partida);

		partida.setGameAtualIndice(0);

		catalogoJogadorService.salvar(jogadorA.orElse(null));
		catalogoJogadorService.salvar(jogadorB.orElse(null));
		this.salvar(partida);

		return partida;
	}

	@Override
	@Transactional
	public Partida iniciarPartida(Long partidaId) {
		Partida partida = this.buscar(partidaId);
		partida.iniciar();
		gestaoGameServiceImpl.iniciarGame(partida.primeiroGameDaPartida());
		partida.setGameAtualIndice(0);
		this.salvar(partida);
		return partida;
	}

	@Override
	public Partida interromperPartida(Long partidaId) {
		Partida partida = this.buscar(partidaId);
		partida.interromper();
		return this.salvar(partida);
	}

	@Override
	@Transactional
	public Partida continuarPartida(Long partidaId) {
		Partida partida = this.buscar(partidaId);
		//TODO corrigir método, continuando partida que foi interrompida (status interrompida)
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
						gestaoGameServiceImpl.iniciarGame(gameEmJogo);
					}
				} else {
					throw new NegocioException("Partida ainda precisa ser iniciada");
				}
			}
		}
		return this.salvar(partida);
	}

	@Override
	@Transactional
	public void finalizarPartida(Partida partida) {
		partida.finalizar();
		this.salvar(partida);
	}

	@Override
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

	@Override
	@Transactional
	public void excluirPartida(Long partidaId) {
		Partida partida = this.buscar(partidaId);
		if (partida.cancelado()) {
			partida.getGames().forEach(game -> {
				game.getPontos().forEach(pontuacao -> gestaoPontuacaoService.excluir(pontuacao));
				game.setPontos(null);
				gestaoGameServiceImpl.excluir(game);
			});
			partida.setGames(null);
			partida.setJogadorA(null);
			partida.setJogadorB(null);
			partida.setPrimeiroSacador(null);
			partida.setGameAtualIndice(-1);
			partidaRepository.delete(partida);
			System.out.println("partida "+partidaId+" deletada");
		} else
			throw new NegocioException("Somente partida CANCELADA pode ser excluída");
	}

	@Override
	@Transactional
	public void setPartidaEmAndamento(Partida partida) {
		partida.setEmAndamento();
		System.out.println("partida "+partida.getId()+" entrou em andamento");
		this.salvar(partida);
	}

	@Override
	public void checaSeJogadoresSelecionadosCorretamente(Partida partida) {

		if (partida.getJogadorA() == null && partida.getJogadorB() == null)
			throw new NegocioException("Nenhum jogador foi selecionado para a partida");
		if (partida.getJogadorA() != null && partida.getJogadorB() == null)
			throw new NegocioException(
					"Somente o jogador " + partida.getJogadorA().getNome() + " foi selecionado para a partida");
		if (partida.getJogadorA() == null && partida.getJogadorB() != null) {
			throw new NegocioException(
					"Somente o jogador " + partida.getJogadorB().getNome() + " foi selecionado para a partida");
		}
	}

	@Override
	public boolean partidaJaTemVencedor(Partida partida) {
		List<Integer> resultado = partida.calculaResultado();
		return resultado.get(JOGADOR_A) == gamesParaVencerPartida(partida)
				|| resultado.get(JOGADOR_B) == gamesParaVencerPartida(partida);
	}

	@Override
	public boolean temGameEmAndamento(Partida partida) {
		Game game;
		for (Game value : partida.getGames()) {
			game = value;
			if (game.emAndamento())
				return true;
		}
		return false;
	}

	@Override
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
			gameOut = gestaoGameServiceImpl.buscar(partidaOut.getGame(i).getId());
			if (gamesIn.size() > i) {
				gameIn = gamesIn.get(i);
				int pontosJogadorA = gameIn.getPontosJogador(JOGADOR_A);
				int pontosJogadorB = gameIn.getPontosJogador(JOGADOR_B);
				gameOut.setPontosJogador(JOGADOR_A, pontosJogadorA);
				gameOut.setPontosJogador(JOGADOR_B, pontosJogadorB);
				if (CalculosGlobais.pontuacaoParaFinalizarGame(pontosJogadorA, pontosJogadorB)) {
					gameOut.finalizar();
					inicioIn = getInicioValido(gameIn, inicioIn);
					fimIn = getFimValido(gameIn, fimIn);
					if (inicioIn.isBefore(fimIn)) {
						gameOut.setInicio(inicioIn);
						gameOut.setFim(fimIn);
					} else
						throw new NegocioException("Data início após data final");
					gestaoGameServiceImpl.salvar(gameOut);
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

	@Override
	public void moverParaProximoGame(Partida partida) {
		partida.moverParaProximoGame();
		this.salvar(partida);
		if (this.partidaJaTemVencedor(partida)) {
			this.finalizarPartida(partida);
		}
	}
}
