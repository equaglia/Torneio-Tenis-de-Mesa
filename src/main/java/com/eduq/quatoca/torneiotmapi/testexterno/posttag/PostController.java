package com.eduq.quatoca.torneiotmapi.testexterno.posttag;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/posts")
public class PostController {

	private PostRepository postRepository;
	private TagRepository tagRepository;
	
	@GetMapping
	public List<Post> listarPosts() {
		return postRepository.findAll();
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Post adicionarPost(@RequestBody Post post) {
		return postRepository.save(post);
	}

	@PutMapping("/{postId}/{tagId}")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public Post addTag(@PathVariable Long postId, @PathVariable Long tagId) {
		Optional<Post> post = postRepository.findById(postId);
		Optional<Tag> tag = tagRepository.findById(tagId);
		post.get().addTag(tag.get());
		
		return postRepository.save(post.get());
	}
}
