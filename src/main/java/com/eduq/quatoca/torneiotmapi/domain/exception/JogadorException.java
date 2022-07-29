package com.eduq.quatoca.torneiotmapi.domain.exception;

import java.io.Serial;

public class JogadorException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = 1L;

	public JogadorException(String message) {
		super(message);
	}
}
