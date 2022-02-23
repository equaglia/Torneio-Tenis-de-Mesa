package com.eduq.quatoca.torneiotmapi.api.assembler;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.eduq.quatoca.torneiotmapi.api.model.GameModel;
import com.eduq.quatoca.torneiotmapi.domain.model.Game;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class GameAssembler {

	private ModelMapper modelMapper;

	public GameModel toModel(Game game) {
		return modelMapper.map(game, GameModel.class);
	}
	
	public List<GameModel> toCollectionModel(List<Game> games){
		return games.stream()
				.map(this::toModel)
				.collect(Collectors.toList());
	}
	
	public Game toEntity(GameModel gameModel) {
		return modelMapper.map(gameModel, Game.class);
	}

//	public Game toEntity(GameInput gameInput) {
//		return modelMapper.map(gameInput, Game.class);
//	}

}
