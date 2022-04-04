package com.eduq.quatoca.torneiotmapi.domain.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;

import com.eduq.quatoca.torneiotmapi.TmapiConfig;
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
		return this.salvar(partida);
	}

	@Transactional
	public Partida continuarPartida(Long partidaId) {
		Partida partida = this.buscar(partidaId);
		if (partida.finalizado()) {
			return partida;
		} else {
			List<Resultado> resultado = Resultado.resultadoCorrente(partida);
//			List<Resultado> resultado = gestaoResultadoService.resultadoCorrente(partida);
			if (partidaJaTemVencedor(resultado)) {
				finalizarPartida(partida);
			} else {
				Game proximoGame = partida.proximoGame();
				if (proximoGame == null) {
					finalizarPartida(partida);
				} else
					gestaoGameService.iniciarGame(proximoGame);
			}
		}
		return this.salvar(partida);
	}

	@Transactional
	public void finalizarPartida(Partida partida) {
		partida.finalizar();
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

}
