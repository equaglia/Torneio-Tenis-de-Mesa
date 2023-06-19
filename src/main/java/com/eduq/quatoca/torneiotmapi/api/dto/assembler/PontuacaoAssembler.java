package com.eduq.quatoca.torneiotmapi.api.dto.assembler;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.eduq.quatoca.torneiotmapi.api.model.PontuacaoModel;
import com.eduq.quatoca.torneiotmapi.domain.model.Pontuacao;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class PontuacaoAssembler {
	
	private ModelMapper modelMapper;

	public PontuacaoModel toModel(Pontuacao pontuacao) {
		return modelMapper.map(pontuacao, PontuacaoModel.class);
	}

	@SuppressWarnings("unused")
	public List<PontuacaoModel> toCollectionModel(List<Pontuacao> pontos){
		return pontos.stream()
				.map(this::toModel)
				.collect(Collectors.toList());
	}

	@SuppressWarnings("unused")
	public Pontuacao toEntity(PontuacaoModel pontuacaoModel) {
		return modelMapper.map(pontuacaoModel, Pontuacao.class);
	}
}
