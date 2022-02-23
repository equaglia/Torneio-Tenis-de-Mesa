package com.eduq.quatoca.torneiotmapi.testexterno.posttag;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/tags")
public class TagController {

	private TagRepository tagRepository;
	
	@GetMapping
	public List<Tag> listarTags() {
		return tagRepository.findAll();
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Tag adicionarTag(@RequestBody Tag tag) {
		return tagRepository.save(tag);
	}
}
