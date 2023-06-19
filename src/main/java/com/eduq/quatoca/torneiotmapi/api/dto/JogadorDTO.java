package com.eduq.quatoca.torneiotmapi.api.dto;

import jakarta.validation.constraints.NotBlank;

public record JogadorDTO(
    Long id, 
    @NotBlank String nome,
	@NotBlank String sobrenome,
	@NotBlank String categoria
    ) {
}
