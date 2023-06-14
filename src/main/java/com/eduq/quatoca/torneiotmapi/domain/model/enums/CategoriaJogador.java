package com.eduq.quatoca.torneiotmapi.domain.model.enums;

public enum CategoriaJogador {
	
	CAT_A("A"), CAT_B("B"), CAT_C("C"), CAT_D("D"), CAT_E("E");

	private String value;

	private CategoriaJogador(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	// toString
	@Override
	public String toString() {
		return value;
	}

}
