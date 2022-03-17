package com.eduq.quatoca.torneiotmapi.domain.model;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
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
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JsonIgnore
	private Jogador jogador;

	private Integer pontos = 0;
	
	public void mais1ponto() {
		this.setPontos(pontos++);
	}
	
	public void menos1ponto() {
		this.setPontos(pontos--);
	}
}
