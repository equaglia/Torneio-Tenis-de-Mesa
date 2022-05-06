package com.eduq.quatoca.torneiotmapi.domain.service;

import java.time.OffsetDateTime;
import java.util.Iterator;
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
import com.eduq.quatoca.torneiotmapi.domain.model.Resultado;
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
	private GestaoResultadoService gestaoResultadoService;

	private static int jogadorA = 0;
	private static int jogadorB = 1;

	private TmapiConfig tmapiConfig;

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
	public Partida prepararPartida(Long jogadorAId, Long jogadorBId) {

		Partida partida = new Partida();

		Optional<Jogador> jogadorA = catalogoJogadorService.buscar(jogadorAId);
		Optional<Jogador> jogadorB = catalogoJogadorService.buscar(jogadorBId);
		partida.addJogador(jogadorA.get());
		partida.addJogador(jogadorB.get());

		checaSeJogadoresSelecionadosCorretamente(partida);

		for (int i = 0; i < tmapiConfig.getNumMaxGames(); i++) {
			partida.addGame(gestaoGameService.prepararGame(jogadorA, jogadorB));
		}

		catalogoJogadorService.salvar(jogadorA.get());
		catalogoJogadorService.salvar(jogadorB.get());
		this.salvar(partida);
		return partida;
	}

	@Transactional
	public Partida iniciarPartida(Long partidaId) {
		Partida partida = this.buscar(partidaId);
		partida.iniciar();
		gestaoGameService.iniciarGame(partida.buscarGameEmAndamento().getId());
		this.salvar(partida);
		return partida;
	}

	@Transactional
	public Partida continuarPartida(Long partidaId) {
		Partida partida = this.buscar(partidaId);
		if (partida.isFinalizado()) {
			return partida;
		} else {
			List<Resultado> resultado = Resultado.resultadoCorrente(partida);
			if (partidaJaTemVencedor(resultado)) {
				finalizarPartida(partida);
			} else {
				Game proximoGame = partida.proximoGame();
				if (partida.isEmAndamento()) {

					if (proximoGame == null) {
						finalizarPartida(partida);
					} else
						gestaoGameService.iniciarGame(proximoGame);
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
		if (partida.isCancelado())
			throw new NegocioException("Partida com id:" + partida.getId() + " já estava cancelada");
		if (partida.isEmAndamento() || partida.isInterrompido()) {
			partida.liberarJogadores();
		}
		partida.cancelar();
		return partida;
	}

	@Transactional
	public void excluirPartida(Long partidaId) {
		Partida partida = this.buscar(partidaId);
		if (partida.isCancelado()) {
			partida.getGames().forEach(game -> {
				game.getPontos().forEach(pontuacao -> {
					gestaoPontuacaoService.excluir(pontuacao);
				});
				game.setPontos(null);
				gestaoGameService.excluir(game);
			});
			partida.setGames(null);
			partida.setJogadores(null);
			partida.setPrimeiroSacador(null);
			partidaRepository.delete(partida);
		} else
			throw new NegocioException("Somente partida CANCELADA pode ser excluída");
	}

	@Transactional
	public void setPartidaEmAndamento(Partida partida) {
		partida.setEmAndamento();
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

	public boolean partidaJaTemVencedor(List<Resultado> resultado) {
		return resultado.get(0).getResultado() == gamesParaVencerPartida()
				|| resultado.get(1).getResultado() == gamesParaVencerPartida();
	}

	private int gamesParaVencerPartida() {
		return tmapiConfig.getNumMaxGames() / 2 + 1;
	}

	public boolean temGameEmAndamento(Partida partida) {
		Game game;
		for (Iterator<Game> i = partida.getGames().iterator(); i.hasNext();) {
			game = i.next();
			if (game.isEmAndamento())
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

		if (partidaJaTemVencedor(gestaoResultadoService.resultadoCorrente(partidaOut))) {
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
}
