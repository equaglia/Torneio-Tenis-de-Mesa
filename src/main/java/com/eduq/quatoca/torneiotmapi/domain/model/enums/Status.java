package com.eduq.quatoca.torneiotmapi.domain.model.enums;

public enum Status {

    ACTIVE("Ativo"), INACTIVE("Inativo");

	private String value;

	private Status(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return value;
	}


}
