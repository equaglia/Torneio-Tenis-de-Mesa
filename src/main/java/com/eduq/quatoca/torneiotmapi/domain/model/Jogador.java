package com.eduq.quatoca.torneiotmapi.domain.model;

import com.eduq.quatoca.torneiotmapi.domain.model.enums.CategoriaJogador;
import com.eduq.quatoca.torneiotmapi.domain.model.enums.StatusJogador;
import com.eduq.quatoca.torneiotmapi.domain.model.enums.converters.CategoriaJogadorConverter;

import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.hibernate.validator.constraints.Length;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

//@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
@ToString
//@RequiredArgsConstructor
@Entity
@SQLDelete(sql = "UPDATE Jogador SET status = 'Inativo' WHERE id = ?")
@Where(clause = "status = 'Ativo'")
public class Jogador {

	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToMany(cascade = 
		{CascadeType.PERSIST,
		CascadeType.MERGE})
	@JoinTable(name = "jogadores_partidas",
		joinColumns = @JoinColumn(name="partida_id"),
		inverseJoinColumns = @JoinColumn(name="jogador_id"))
	@ToString.Exclude
	private Set<Partida> partidas = new HashSet<>();
	
	@NotBlank(message = "Nome é mandatório")
	@Column(nullable = false)
	private String nome;
	
	@NotBlank(message = "Sobrenome é mandatório")
	@Column(nullable = false)
	private String sobrenome;
	
	@NotNull(message = "Categoria é mandatório")
	@Convert(converter = CategoriaJogadorConverter.class)
	private CategoriaJogador categoria;
	
	@NotNull(message = "Status é mandatório")
	@Enumerated(EnumType.STRING)
	private StatusJogador statusJogador;
	
	@OneToMany(mappedBy = "jogador", cascade = CascadeType.ALL)
	@ToString.Exclude
	private Set<Pontuacao> pontos;

	@NotNull
	@Length(max = 10)
	@Pattern(regexp = "Ativo|Inativo")
	@Column(nullable = false, length = 10)
	private String status = "Ativo";

	public Jogador() {
		super();
		this.setStatusJogador(StatusJogador.Disponivel);
	}
	
	public boolean disponivel() {
		return this.getStatusJogador() == StatusJogador.Disponivel;
	}
	
	public void convocar() {
		this.setStatusJogador(StatusJogador.NaoDisponivel);
	}
	
	public void liberar() {
		this.setStatusJogador(StatusJogador.Disponivel);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
		Jogador jogador = (Jogador) o;
		return id != null && Objects.equals(id, jogador.id);
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}
