package com.eduq.quatoca.torneiotmapi.domain.service.impl;

import java.text.MessageFormat;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;
import javax.validation.Valid;

import com.eduq.quatoca.torneiotmapi.domain.model.enums.StatusPartida;
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
	private GestaoGameService gestaoGameService;
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
		Collections.sort(partida.getGames());
		System.out.println(partida);
		return partidaRepository.save(partida);
	}

	@Override
	@Transactional
	public Partida prepararPartida(Long jogadorAId, Long jogadorBId, int quantidadeGames) {

		Partida partida = new Partida();

		partida.setQuantidadeGames(quantidadeGames);

		Optional<Jogador> jogadorA = catalogoJogadorService.buscar(jogadorAId);
		Optional<Jogador> jogadorB = catalogoJogadorService.buscar(jogadorBId);
		this.setAdversarios(partida, jogadorA.orElse(null), jogadorB.orElse(null));

		//		for (int i = 1; i <= quantidadeGames; i++) {
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

	@Override
	@Transactional
	public Partida iniciarPartida(Long partidaId) {
		Partida partida = this.buscar(partidaId);
		partida.iniciar();
		gestaoGameService.iniciarGame(partida.primeiroGameDaPartida());
		partida.setGameAtualIndice(0);
		this.salvar(partida);
		return partida;
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
						gestaoGameService.iniciarGame(gameEmJogo);
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
	public Partida interromperPartida(Long partidaId) {
		Partida partida = this.buscar(partidaId);
		switch (partida.getStatus()) {
			case EmAndamento -> {
				partida.liberarJogadores();
				partida.interromper();
			}
			case Interrompida, Cancelada, Preparada, Finalizada ->
					throw (new NegocioException("Somente partida Em Andamento pode ser interrompida"));
			default -> throw (new NegocioException("Ops, algo deu errado..."));
		}
		return this.salvar(partida);
	}

	@Override
	@Transactional
	public Partida retornarPartidaInterrompida(Long partidaId) {
		Partida partida = this.buscar(partidaId);
		switch (partida.getStatus()) {
			case Interrompida -> {
				partida.convocarJogadores();
				partida.setEmAndamento();
			}
			case EmAndamento, Cancelada, Preparada, Finalizada ->
					throw (new NegocioException("Somente partida Interrompida pode retornar"));
			default -> throw (new NegocioException("Ops, algo deu errado..."));
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
				gestaoGameService.excluir(game);
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
		if (partida.getStatus() == StatusPartida.Finalizada) {
			partida.setEmAndamento();
			partida.convocarJogadores();
			partida.getGames().forEach(game -> {
				if (game.cancelado()) {
					game.setPreparado();
				}
			});
			this.garantirNoMaximoUmGameEmAndamento(partida);
			partida.setFim(null);
		}
		this.salvar(partida);
	}

	private void setAdversarios(Partida partida, Jogador jogA, Jogador jogB) {

//		if (jogA != null && jogB != null)
//		{
/*			if (partida.getPrimeiroSacador() == null) {
				partida.setPrimeiroSacador(jogA);
			} else throw new NegocioException("Inconsistência na seleção do primeiro sacador");*/
			partida.setPrimeiroSacador(jogA);
			partida.setJogadorA(jogA);
			partida.setJogadorB(jogB);
			jogA.getPartidas().add(partida);
			jogB.getPartidas().add(partida);
			checaSeJogadoresSelecionadosCorretamente(partida);
//		} else {
//			throw new NegocioException("Os dois jogadores da partida já haviam sido selecionados");
//		}
	}

	private void garantirNoMaximoUmGameEmAndamento(Partida partida) {
//		boolean temGameEmAndamento = false;
		for (int i = 0; i < partida.getQuantidadeGames(); i++) {
			Game game = partida.getGame(i);
			if (game.emAndamento()) {
//				this.setGameAtual(game);
				partida.setGameAtualIndice(i); //TODO Checar alteração de gameAtualIndice
				System.out.println("Partida.garantirNoMaximoUmGameEmAndamento"+" atualizou gameAtualIndice para "+i);
				break;
//				if (!temGameEmAndamento) temGameEmAndamento = true;
//				else {
//					game.setStatus(StatusJogo.Preparado);
//				}
			}
		}
	}

	@Override
	public void checaSeJogadoresSelecionadosCorretamente(Partida partida) {

		if (partida.getJogadorA() == null && partida.getJogadorB() == null)
			throw new NegocioException("Nenhum jogador foi selecionado para a partida");
		if (partida.getJogadorA() != null && partida.getJogadorB() == null)
			throw new NegocioException(MessageFormat.format("Somente o jogador {0} foi selecionado para a partida", partida.getJogadorA().getNome()));
		if ((partida.getJogadorA() == null) && (partida.getJogadorB() != null)) {
			throw new NegocioException(MessageFormat.format("Somente o jogador {0} foi selecionado para a partida", partida.getJogadorB().getNome()));
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
			gameOut = gestaoGameService.buscar(partidaOut.getGame(i).getId());
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

	@Override
	public void moverParaProximoGame(Partida partida) {
		partida.moverParaProximoGame();
		this.salvar(partida);
		if (this.partidaJaTemVencedor(partida)) {
			this.finalizarPartida(partida);
		}
	}

	public Game proximoGame(Partida partida) {
		Collections.sort(partida.getGames());

		Game gameEmAndamento = partida.buscarGameEmAndamento();
		if (partida.emAndamento() && gameEmAndamento == null) partida.finalizar();
		return gameEmAndamento;
	}

	@Override
	public boolean isUltimoGameDaPartida(Game game) {
		Partida partida = buscar(game.getPartida().getId());
		int indiceGame = game.getNumero();
		if (indiceGame == partida.getQuantidadeGames()-1) {
			return true;
		} else return partida.getGames().get(indiceGame + 1).cancelado();
	}


}
