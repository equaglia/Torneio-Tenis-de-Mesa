package com.eduq.quatoca.torneiotmapi.domain.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
@Entity
@Embeddable
public class Pontuacao {
	
	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(cascade=CascadeType.ALL)
	@JsonIgnore
	private Game game;
	
//	@ManyToOne(cascade = CascadeType.ALL)
	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
	@JsonIgnore
	private Jogador jogador;

	private int pontos = 0;
	
	public int maisUmPonto() {return this.getPontos() + 1;}
	
	public int menosUmPonto() {
		return this.getPontos() - 1;
	}

	@Override
	public String toString() {
		return "" + this.getPontos();
	}
}
