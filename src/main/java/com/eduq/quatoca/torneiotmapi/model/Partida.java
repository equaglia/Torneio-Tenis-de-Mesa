package com.eduq.quatoca.torneiotmapi.model;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import lombok.Data;

@Data
@Entity
public class Partida {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToMany(mappedBy = "partidas", cascade = CascadeType.ALL)
	private List<Jogador> jogadores;
	
	@OneToMany(mappedBy = "partida", cascade = CascadeType.ALL)
	@Embedded
	private List<Game> games;

	private OffsetDateTime inicio;
	private OffsetDateTime fim;
}
