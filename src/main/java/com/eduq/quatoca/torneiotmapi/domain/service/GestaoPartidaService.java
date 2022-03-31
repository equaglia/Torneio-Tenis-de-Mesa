package com.eduq.quatoca.torneiotmapi.domain.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.eduq.quatoca.torneiotmapi.domain.exception.EntidadeNaoEncontradaException;
import com.eduq.quatoca.torneiotmapi.domain.model.Jogador;
import com.eduq.quatoca.torneiotmapi.domain.model.Partida;
import com.eduq.quatoca.torneiotmapi.domain.repository.PartidaRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class GestaoPartidaService {

	private CatalogoJogadorService catalogoJogadorService;
	private PartidaRepository partidaRepository;
	private GestaoGameService gestaoGameService;
//	private Integer numMaxGames = 5;
//	public int getNumMaxGames() {
//		return numMaxGames;
//	}
//
//	public void setNumMaxGames(int numMaxGames) {
//		this.numMaxGames = numMaxGames;
//	}

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

		int numDeGames = 5;

		Partida partida = new Partida();

		Optional<Jogador> jogadorA = catalogoJogadorService.buscar(jogadorAId);
		Optional<Jogador> jogadorB = catalogoJogadorService.buscar(jogadorBId);
		partida.addJogador(jogadorA.get());
		partida.addJogador(jogadorB.get());

		for (int i = 0; i < numDeGames; i++) {
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
}
