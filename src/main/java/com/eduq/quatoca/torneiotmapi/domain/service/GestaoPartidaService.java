package com.eduq.quatoca.torneiotmapi.domain.service;

import java.time.OffsetDateTime;
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

	public Partida buscar(Long partidaId) { //TBD usar este método
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
		OffsetDateTime horarioInicial = OffsetDateTime.now();
		
		Partida partida = new Partida();

		Optional<Jogador> jogadorA = catalogoJogadorService.buscar(jogadorAId);
		Optional<Jogador> jogadorB = catalogoJogadorService.buscar(jogadorBId);
		partida.addJogador(jogadorA.get());
		partida.addJogador(jogadorB.get());

		partida.addGame(gestaoGameService.prepararGame(jogadorA, jogadorB, horarioInicial));
		for (int i = 1; i < 2; i++) {
			partida.addGame(gestaoGameService.prepararGame(jogadorA, jogadorB));
		}

		catalogoJogadorService.salvar(jogadorA.get());
		catalogoJogadorService.salvar(jogadorB.get());
		partida.setInicio(horarioInicial);
		this.salvar(partida);
		return partida;
	}
}
