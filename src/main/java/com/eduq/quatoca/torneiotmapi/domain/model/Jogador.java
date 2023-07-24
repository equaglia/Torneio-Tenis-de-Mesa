package com.eduq.quatoca.torneiotmapi.domain.model;

import com.eduq.quatoca.torneiotmapi.domain.model.enums.CategoriaJogador;
import com.eduq.quatoca.torneiotmapi.domain.model.enums.Status;
import com.eduq.quatoca.torneiotmapi.domain.model.enums.StatusJogador;
import com.eduq.quatoca.torneiotmapi.domain.model.enums.converters.CategoriaJogadorConverter;
import com.eduq.quatoca.torneiotmapi.domain.model.enums.converters.StatusConverter;

import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

//@EqualsAndHashCode(onlyExplicitlyIncluded = true)
// @Getter
// @Setter
// @ToString
//@RequiredArgsConstructor
@Data
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
    @Column(length = 10, nullable = false)
	@Convert(converter = CategoriaJogadorConverter.class)
	private CategoriaJogador categoria;
	
	@NotNull(message = "Status é mandatório")
	@Enumerated(EnumType.STRING)
	private StatusJogador statusJogador = StatusJogador.Disponivel;
	
	@OneToMany(mappedBy = "jogador", cascade = CascadeType.ALL)
	@ToString.Exclude
	private Set<Pontuacao> pontos;

	@NotNull
	@Column(nullable = false, length = 10)
	@Convert(converter = StatusConverter.class)
	private Status status = Status.ACTIVE;

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

	
	public Optional<Set<Partida>> getPartidasOptional() {
		return Optional.of(getPartidas());
	}
}
