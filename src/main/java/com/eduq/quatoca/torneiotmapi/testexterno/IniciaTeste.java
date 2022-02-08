package com.eduq.quatoca.torneiotmapi.testexterno;

public class IniciaTeste {

	private PostRepository postRepository;
	private TagRepository tagRepository;

	public static void main(String[] args) {
		
		
		   Post post1 = new Post("JPA with Hibernate");
		    Post post2 = new Post("Native Hibernate");
		    
		    Tag tag1 = new Tag("Java");
		    Tag tag2 = new Tag("Hibernate");
		 		    
		    post1.addTag(tag1);
		    post1.addTag(tag2);
		 
		    post2.addTag(tag1);
		 
	}

}
