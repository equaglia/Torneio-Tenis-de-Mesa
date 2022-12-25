package com.eduq.quatoca.torneiotmapi.api.assembler;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.eduq.quatoca.torneiotmapi.api.model.PartidaResumoModel;
import com.eduq.quatoca.torneiotmapi.domain.model.Partida;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class PartidaResumoAssembler {
	private ModelMapper modelMapper;

	public PartidaResumoModel toResumoModel(Partida partida)
	{
		return modelMapper.map(partida, PartidaResumoModel.class);
	}
	
	public List<PartidaResumoModel> toCollectionResumoModel(List<Partida> partidas){
		return partidas.stream()
				.map(this::toResumoModel)
				.collect(Collectors.toList());
	}
	
	public Partida toEntity(PartidaResumoModel partidaResumoModel) {
		return modelMapper.map(partidaResumoModel, Partida.class);
	}
}
