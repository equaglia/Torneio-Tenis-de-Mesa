package com.eduq.quatoca.torneiotmapi.api.assembler;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.eduq.quatoca.torneiotmapi.api.model.JogadorPartidasModel;
import com.eduq.quatoca.torneiotmapi.domain.model.Jogador;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class JogadorPartidasAssembler {
	
	private ModelMapper modelMapper;

	public JogadorPartidasModel toModel(Jogador jogador) {
		return modelMapper.map(jogador, JogadorPartidasModel.class);
	}
	
	public List<JogadorPartidasModel> toCollectionModel(List<Jogador> jogadores){
		return jogadores.stream()
				.map(this::toModel)
				.collect(Collectors.toList());
	}
	
	public Jogador toEntity(JogadorPartidasModel jogadorPartidasModel) {
		return modelMapper.map(jogadorPartidasModel, Jogador.class);
	}

}
