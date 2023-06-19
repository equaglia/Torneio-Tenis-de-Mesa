package com.eduq.quatoca.torneiotmapi.api.dto.assembler;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.eduq.quatoca.torneiotmapi.api.model.JogadorModel;
import com.eduq.quatoca.torneiotmapi.api.model.input.JogadorInput;
import com.eduq.quatoca.torneiotmapi.domain.model.Jogador;
import com.eduq.quatoca.torneiotmapi.domain.model.enums.CategoriaJogador;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class JogadorAssembler {

	private ModelMapper modelMapper;

	public JogadorModel toModel(Jogador jogador) {
		return modelMapper.map(jogador, JogadorModel.class);
	}
	
	public List<JogadorModel> toCollectionModel(List<Jogador> jogadores){
		return jogadores.stream()
				.map(this::toModel)
				.collect(Collectors.toList());
	}
	
	public Jogador toEntity(JogadorInput jogadorInput) {
		System.out.println("JogadorAssembler.toEntity: jogadorInput "+jogadorInput.toString());
		Jogador jogador = modelMapper.map(jogadorInput, Jogador.class);
		jogador.setCategoria(convertCategoriaJogadorValue(jogadorInput.getCategoria()));
		System.out.println("JogadorAssembler.toEntity: jogador "+jogador.toString());
		return jogador;
	}

	public CategoriaJogador convertCategoriaJogadorValue(String value) {
		if (value == null) {
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
