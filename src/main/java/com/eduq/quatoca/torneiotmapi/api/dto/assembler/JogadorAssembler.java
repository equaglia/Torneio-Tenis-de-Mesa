package com.eduq.quatoca.torneiotmapi.api.dto.assembler;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.eduq.quatoca.torneiotmapi.api.dto.JogadorDTO;
import com.eduq.quatoca.torneiotmapi.api.model.JogadorModel;
import com.eduq.quatoca.torneiotmapi.api.model.input.JogadorInput;
import com.eduq.quatoca.torneiotmapi.domain.model.Jogador;
import com.eduq.quatoca.torneiotmapi.domain.model.enums.CategoriaJogador;
import com.eduq.quatoca.torneiotmapi.domain.model.enums.Status;
import com.eduq.quatoca.torneiotmapi.domain.model.enums.StatusJogador;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class JogadorAssembler {

	private ModelMapper modelMapper;

	// public JogadorDTO toModel(Jogador jogador) {
		// return modelMapper.map(jogador, JogadorDTO.class);
	public JogadorModel toModel(Jogador jogador) {
		return modelMapper.map(jogador, JogadorModel.class);
	}

	// public List<JogadorDTO> toCollectionModel(List<Jogador> jogadores){
		public List<JogadorModel> toCollectionModel(List<Jogador> jogadores){
		return jogadores.stream()
				.map(this::toModel)
				.collect(Collectors.toList());
	}

	// public Jogador toEntity(JogadorDTO jogadorDTOInput) {
	public Jogador toEntity(JogadorModel jogadorDTOInput) {
		
		// System.out.println("JogadorAssembler.toEntity: jogadorInput "+jogadorDTOInput.toString());
		Jogador jogador = modelMapper.map(jogadorDTOInput, Jogador.class);
		// System.out.println("JogadorAssembler.toEntity: jogador "+jogador.toString());
		jogador.setCategoria(convertCategoriaJogadorValue(jogadorDTOInput.getCategoria().toString()));
		jogador.setStatusJogador(StatusJogador.Disponivel);

		System.out.println("JogadorAssembler.toEntity: jogador "+jogador.toString());
		return jogador;
	}

	public CategoriaJogador convertCategoriaJogadorValue(String value) {
		System.out.println("JogadorAssembler.convertCategoriaJogadorValue: value = "+value);
		if (value == null) {
		// System.out.println("JogadorAssembler.convertCategoriaJogadorValue: value = null "+value);
			return null;
		}
		return switch (value) {
			case "A" -> CategoriaJogador.CAT_A;
			case "B" -> CategoriaJogador.CAT_B;
			case "C" -> CategoriaJogador.CAT_C;
			case "D" -> CategoriaJogador.CAT_D;
			case "E" -> CategoriaJogador.CAT_E;
			default -> throw new IllegalArgumentException("Categoria inválida: " + value);
		};
	}

}
