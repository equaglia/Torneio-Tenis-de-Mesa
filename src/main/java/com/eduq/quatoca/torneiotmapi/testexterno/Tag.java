package com.eduq.quatoca.torneiotmapi.testexterno;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.NaturalId;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
@Entity
public class Tag {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank
	@NaturalId
	private String name;
	
	public Tag() {}
	
	public Tag(String name) {
		this.name = name;
	}
	
	@ManyToMany(mappedBy = "tags")
	@JsonIgnore
	private Set<Post> posts = new HashSet<>();
}
