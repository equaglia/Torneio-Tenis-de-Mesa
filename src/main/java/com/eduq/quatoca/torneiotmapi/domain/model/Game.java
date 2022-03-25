package com.eduq.quatoca.torneiotmapi.domain.model;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import com.eduq.quatoca.torneiotmapi.domain.exception.NegocioException;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@Entity
@Embeddable
public class Game {

	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(cascade=CascadeType.ALL)
	@JsonIgnore
	private Partida partida;

	@OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
	@Embedded
	private List<Pontuacao> pontos = new ArrayList<>();
	
	private OffsetDateTime inicio;
	private OffsetDateTime fim;

	@NotNull(message = "Status do game é mandatório")
	@Enumerated(EnumType.STRING)
	private StatusJogo status;
	
	public void addPontuacao(Pontuacao pontuacao, Jogador jogador) {
		pontos.add(pontuacao);
		pontuacao.setGame(this);
		pontuacao.setJogador(jogador);
	}

	public Game() {
		super();
		this.status = StatusJogo.Preparado;
	}
	

	public void iniciar() {
		switch (this.getStatus()) {
		case Preparado:
			this.setStatus(StatusJogo.EmAndamento);
			break;
		case EmAndamento:
			new NegocioException("Game não pode ser iniciado, pois já estava Em Andamento");
			break;
		case Cancelado:
			new NegocioException("Game Cancelado não pode ser iniciado");
			break;
		case Finalizado:
			new NegocioException("Game já foi Finalizado então não pode ser iniciado");
			break;
		case Interrompido:
			new NegocioException("Game Interrompido precisa voltar para o status Preparado para ser reiniciado");
			break;
		default:
			new NegocioException("Ops, algo deu errado...");
			break;
		}	}
	
	public void finalizar() {
		this.setStatus(StatusJogo.Finalizado);
	}
	
	public Boolean preparado() {
		return this.getStatus() == StatusJogo.Preparado;
	}

	public Boolean emAndamento() {
		return this.getStatus() == StatusJogo.EmAndamento;
	}

	public Boolean finalizado() {
		return this.getStatus() == StatusJogo.Finalizado;
	}

	public Boolean interrompido() {
		return this.getStatus() == StatusJogo.Interrompido;
	}

	public Boolean cancelado() {
		return this.getStatus() == StatusJogo.Cancelado;
	}
	
	public Boolean emCurso() {
		return this.getPartida().buscarGameEmAndamento() == this;
	}

}