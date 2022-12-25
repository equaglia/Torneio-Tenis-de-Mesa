package com.eduq.quatoca.torneiotmapi.api.assembler;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.eduq.quatoca.torneiotmapi.api.model.PartidaModel;
import com.eduq.quatoca.torneiotmapi.domain.model.Partida;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class PartidaAssembler {

	private ModelMapper modelMapper;

	public PartidaModel toModel(Partida partida) {
		return modelMapper.map(partida, PartidaModel.class);
	}
	
	public List<PartidaModel> toCollectionModel(List<Partida> partidas){
		return partidas.stream()
				.map(this::toModel)
				.collect(Collectors.toList());
	}
	
	public Partida toEntity(PartidaModel partidaModel)
	{
		return modelMapper.map(partidaModel, Partida.class);
	}
}
