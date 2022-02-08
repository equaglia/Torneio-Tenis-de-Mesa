package com.eduq.quatoca.torneiotmapi.model;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Builder.Default;
import lombok.Data;

@Data
@Entity
@Embeddable
public class Pontuacao {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(cascade=CascadeType.ALL)
	private Game game;

	@ManyToOne(cascade=CascadeType.ALL)
	private Jogador jogador;

	
	private Integer pontos = 0;
}
