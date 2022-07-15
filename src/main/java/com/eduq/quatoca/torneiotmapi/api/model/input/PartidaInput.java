package com.eduq.quatoca.torneiotmapi.api.model.input;

import java.time.OffsetDateTime;

import lombok.Data;

@Data
public class PartidaInput { //TODO definir uso

	private OffsetDateTime inicio;
	private OffsetDateTime fim;

}
