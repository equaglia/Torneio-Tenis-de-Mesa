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
		if (!this.getPartida().isEmAndamento()){
			throw(new NegocioException("Game não pode iniciar, pois partida não está em andamento"));
		} else {
			switch (this.getStatus()) {
			case Preparado:
				this.setStatus(StatusJogo.EmAndamento);
				this.setInicio(OffsetDateTime.now());
				break;
			case EmAndamento:
				break;
			case Cancelado:
				throw(new NegocioException("Game Cancelado não pode ser iniciado"));
			case Finalizado:
				throw(new NegocioException("Game já Finalizado não pode ser iniciado"));
			case Interrompido:
				throw(new NegocioException("Game Interrompido precisa voltar para o status Preparado para ser reiniciado"));
			default:
				throw(new NegocioException("Ops, algo deu errado..."));
			}
		}
	}
	
	public void finalizar() {
		this.setStatus(StatusJogo.Finalizado);
		this.setFim(OffsetDateTime.now());
	}
	
	public void setPreparado() {
		this.setStatus(StatusJogo.Preparado);
		this.setFim(null);		
	}

	public boolean isPreparado() {
		return this.getStatus() == StatusJogo.Preparado;
	}

	public boolean isEmAndamento() {
		return this.getStatus() == StatusJogo.EmAndamento;
	}

	public boolean isFinalizado() {
		return this.getStatus() == StatusJogo.Finalizado;
	}

	public boolean isInterrompido() {
		return this.getStatus() == StatusJogo.Interrompido;
	}

	public boolean isCancelado() {
		return this.getStatus() == StatusJogo.Cancelado;
	}
	
	public boolean isEmCurso() {
		return this.getPartida().buscarGameEmAndamento() == this;
	}

	public void cancelar() {
		this.setStatus(StatusJogo.Cancelado);
	}

	public void setEmAndamento() {
		this.setStatus(StatusJogo.EmAndamento);
		this.setFim(null);		
	}
	
	public int getPontosJogador(int i) {
		return this.getPontos().get(i).getPontos();
	}
	
	public void setPontosJogador(int i, int pontuacao) {
		this.getPontos().get(i).setPontos(pontuacao);
	}
}