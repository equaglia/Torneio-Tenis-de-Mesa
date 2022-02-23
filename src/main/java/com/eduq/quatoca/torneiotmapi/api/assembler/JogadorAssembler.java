package com.eduq.quatoca.torneiotmapi.api.assembler;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.eduq.quatoca.torneiotmapi.api.model.JogadorModel;
import com.eduq.quatoca.torneiotmapi.api.model.input.JogadorInput;
import com.eduq.quatoca.torneiotmapi.domain.model.Jogador;

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
		return modelMapper.map(jogadorInput, Jogador.class);
	}

}
