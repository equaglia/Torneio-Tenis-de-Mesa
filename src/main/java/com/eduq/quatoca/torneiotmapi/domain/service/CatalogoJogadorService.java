package com.eduq.quatoca.torneiotmapi.domain.service;

import java.util.List;
import java.util.Optional;

// import com.eduq.quatoca.torneiotmapi.domain.service.CatalogoJogadorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.eduq.quatoca.torneiotmapi.api.dto.JogadorDTO;
import com.eduq.quatoca.torneiotmapi.api.dto.assembler.JogadorAssembler;
import com.eduq.quatoca.torneiotmapi.api.model.JogadorModel;
// import com.eduq.quatoca.torneiotmapi.api.model.JogadorModel;
// import com.eduq.quatoca.torneiotmapi.api.model.input.JogadorInput;
import com.eduq.quatoca.torneiotmapi.domain.exception.EntidadeNaoEncontradaException;
import com.eduq.quatoca.torneiotmapi.domain.exception.NegocioException;
import com.eduq.quatoca.torneiotmapi.domain.model.Jogador;
import com.eduq.quatoca.torneiotmapi.domain.repository.JogadorRepository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Validated
@Service
public class CatalogoJogadorService {

	private JogadorRepository jogadorRepository;
	private JogadorAssembler jogadorAssembler;
	
	// public List<JogadorDTO> listar() {
	public List<JogadorModel> listar() {
		return jogadorAssembler.toCollectionModel(jogadorRepository.findAll());
	}

	// public Optional<JogadorDTO> buscar(Long jogadorId) {
	public Optional<JogadorModel> buscar(Long jogadorId) {
		return Optional.of(jogadorAssembler.toModel(jogadorRepository.findById(jogadorId)
			.orElseThrow(() -> new EntidadeNaoEncontradaException("Jogador não encontrado CatalogoJogadorService"))));
	}		

	@Transactional
	// public JogadorDTO salvar(@Valid @NotNull JogadorDTO jogador) {
	public JogadorModel salvar(@Valid @NotNull JogadorModel jogador) {
		System.out.println("JogadorService.salvar: "+jogador.toString());
		return jogadorAssembler.toModel(jogadorRepository.save(jogadorAssembler.toEntity(jogador)));
	}

	// @Transactional
	// public Jogador salvar(JogadorInput jogador) {
	// // public Jogador salvar(Jogador jogador) {
	// 	System.out.println("JogadorService.salvar: "+jogador.toString());
	// 	// TODO TBD controles, tipo: email, fone
	// 	// return jogadorRepository.save(jogador);
	// 	return jogadorAssembler.toEntity(jogador);
	// }

	@Transactional
	// public JogadorDTO atualizar(@NotNull Long jogadorId, @Valid JogadorDTO jogadorDTO) {
	public JogadorModel atualizar(@NotNull Long jogadorId, @Valid JogadorModel jogadorDTO) {
		return jogadorRepository.findById(jogadorId)
			.map(record -> {
				// record.setNome(jogadorDTO.nome());
				// record.setSobrenome(jogadorDTO.sobrenome());
				record.setNome(jogadorDTO.getNome());
				record.setSobrenome(jogadorDTO.getSobrenome());
				// record.setCategoria("null");
				record.setCategoria( jogadorAssembler.convertCategoriaJogadorValue(jogadorDTO.getCategoria().toString() ));
				return jogadorAssembler.toModel(jogadorRepository.save(record));
			}).orElseThrow(() -> new EntidadeNaoEncontradaException("Jogador não encontrado"));
			
	}


	@Transactional
	public void excluir(@NotNull Long jogadorId) {
		// Jogador jogadorParaExcluir = this.buscar(jogadorId).orElse(null);
		Optional<Jogador> jogadorParaExcluir = jogadorRepository.findById(jogadorId);

		if (jogadorParaExcluir == null)
			throw (new NegocioException("Jogador não existente"));
		else if (jogadorParaExcluir.get().getPartidas().isEmpty()) {
			jogadorRepository.deleteById(jogadorId);
			new ResponseEntity<>("Jogador deletado do sistema", HttpStatus.NO_CONTENT);
		} else {
			throw(new NegocioException("Jogador com partida registrada não pode ser deletado."));
		}
	}
}
