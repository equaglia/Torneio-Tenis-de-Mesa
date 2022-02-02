package com.eduq.quatoca.torneiotmapi.model;

import java.time.OffsetDateTime;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import lombok.Data;

@Data
@Entity
@Embeddable
public class Game {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	private Partida partida;

	@OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
	@Embedded
	private Set<Pontuacao> pontos;
	
	private OffsetDateTime inicio;
	private OffsetDateTime fim;
}