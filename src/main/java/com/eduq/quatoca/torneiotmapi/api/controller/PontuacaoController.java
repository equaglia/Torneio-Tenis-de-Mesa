package com.eduq.quatoca.torneiotmapi.api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eduq.quatoca.torneiotmapi.domain.model.Pontuacao;
import com.eduq.quatoca.torneiotmapi.domain.repository.PontuacaoRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/pontos")
@Tag(name = "PONTUAÇÃO", description = "Informações de pontuação")
public class PontuacaoController {

	private PontuacaoRepository pontuacaoRepository; 
	
	@Operation(summary = "Lista das pontuações de jogador ???",
			description = "Listar pontuações de jogador ???")//TODO pontuaçao de quem???
	@GetMapping
	public List<Pontuacao> listar() {
		return pontuacaoRepository.findAll();	
	}


//	TODO GET pontuacao da partida. TBD
//	TODO GET pontuacao do game. TBD
//	TODO GET pontuacao do jogador na partida. TBD
}
