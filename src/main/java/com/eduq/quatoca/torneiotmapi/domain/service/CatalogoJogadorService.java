package com.eduq.quatoca.torneiotmapi.domain.service;

import java.util.List;
import java.util.Optional;

// import com.eduq.quatoca.torneiotmapi.domain.service.CatalogoJogadorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eduq.quatoca.torneiotmapi.api.dto.assembler.JogadorAssembler;
import com.eduq.quatoca.torneiotmapi.api.model.JogadorModel;
import com.eduq.quatoca.torneiotmapi.api.model.input.JogadorInput;
import com.eduq.quatoca.torneiotmapi.domain.exception.EntidadeNaoEncontradaException;
import com.eduq.quatoca.torneiotmapi.domain.exception.NegocioException;
import com.eduq.quatoca.torneiotmapi.domain.model.Jogador;
import com.eduq.quatoca.torneiotmapi.domain.repository.JogadorRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class CatalogoJogadorService {

	private JogadorRepository jogadorRepository;
	private JogadorAssembler jogadorAssembler;
	
	public Optional<JogadorModel> buscar(Long jogadorId) {
		// return Optional.of(jogadorRepository.findById(jogadorId)
		Jogador jogador = jogadorRepository.findById(jogadorId).orElseThrow(() -> new EntidadeNaoEncontradaException("Jogador não encontrado CatalogoJogadorService"));
		return Optional.of(jogadorAssembler.toModel(jogador));
				// .orElseThrow(() -> new EntidadeNaoEncontradaException("Jogador não encontrado CatalogoJogadorService")));
	}
	
	public List<Jogador> listar() {
		return jogadorRepository.findAll();
	}

	@Transactional
	public Jogador salvar(JogadorInput jogador) {
	// public Jogador salvar(Jogador jogador) {
		System.out.println("JogadorService.salvar: "+jogador.toString());
		// TODO TBD controles, tipo: email, fone
		// return jogadorRepository.save(jogador);
		return jogadorAssembler.toEntity(jogador);
	}

	@Transactional
	public void excluir(Long jogadorId) {
		Jogador jogadorParaExcluir = this.buscar(jogadorId).orElse(null);

		if (jogadorParaExcluir == null)
			throw (new NegocioException("Jogador não existente"));
		else if (jogadorParaExcluir.getPartidas().isEmpty()) {
			jogadorRepository.deleteById(jogadorId);
			new ResponseEntity<>("Jogador deletado do sistema", HttpStatus.NO_CONTENT);
		} else {
			throw(new NegocioException("Jogador com partida registrada não pode ser deletado."));
		}
	}
}
