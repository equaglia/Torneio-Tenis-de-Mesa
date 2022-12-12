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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
@Entity
@Embeddable
public class Game implements Comparable<Game>{

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

//	@Setter(value = AccessLevel.NONE)
	private Integer numero; // TODO Atualizar Swagger
	private OffsetDateTime inicio;
	private OffsetDateTime fim;

	@NotNull(message = "Status do game é mandatório")
	@Enumerated(EnumType.STRING)
	private StatusGame status;
	
	public void addPontuacao(Pontuacao pontuacao, Jogador jogador) {
		pontos.add(pontuacao);
		pontuacao.setGame(this);
		pontuacao.setJogador(jogador);
	}

	public Game() {
		super();
		this.setStatus(StatusGame.Preparado);
	}

	public void iniciar() {
		System.out.println("Game.iniciar() entrada "+this);
		if (!this.getPartida().emAndamento()){
			throw(new NegocioException("Game não pode iniciar, pois partida não está em andamento"));
		} else {
			switch (this.getStatus()) {
				case Preparado:
					this.setStatus(StatusGame.EmAndamento);
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
			System.out.println("Game.iniciar() saida "+this);
		}
	}
	
	public void finalizar() {
		this.setStatus(StatusGame.Finalizado);
		this.setFim(OffsetDateTime.now());
	}
	
	public void setPreparado() {
		this.setStatus(StatusGame.Preparado);
		this.setFim(null);		
	}

	public boolean preparado() {
		return this.getStatus() == StatusGame.Preparado;
	}

	public boolean emAndamento() {
		return this.getStatus() == StatusGame.EmAndamento;
	}

	public boolean finalizado() {
		return this.getStatus() == StatusGame.Finalizado;
	}

	public boolean interrompido() {
		return this.getStatus() == StatusGame.Interrompido;
	}

	public boolean cancelado() {
		return this.getStatus() == StatusGame.Cancelado;
	}
	
	public boolean emCurso() {
		return this.getPartida().buscarGameEmAndamento() == this;
	}

	public void cancelar() {
		this.setStatus(StatusGame.Cancelado);
	}

	public void setEmAndamento() {
		this.setStatus(StatusGame.EmAndamento);
		this.setFim(null);		
	}
	
	public int getPontosJogador(int i) {
		return this.getPontos().get(i).getPontos();
	}
	
	public void setPontosJogador(int i, int pontuacao) {
		this.getPontos().get(i).setPontos(pontuacao);
	}

	@Override
	public String toString() {
		String gameToString = "";
		gameToString = gameToString +"\n g"+"id" + this.getId()+ ": ";
		gameToString = gameToString + this.getPontos();
		gameToString = gameToString + " " + this.getStatus();
		return gameToString;
	}

	@Override
	public int compareTo(Game g) {
		return this.getNumero().compareTo(g.getNumero());
//		return this.getId().compareTo(g.getId());
//		return this.getNumero().compareTo(g.getNumero());
	}
}