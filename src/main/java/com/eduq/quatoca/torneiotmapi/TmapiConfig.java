package com.eduq.quatoca.torneiotmapi;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "config")
public class TmapiConfig {
	
	int numMaxGames;

	public int getNumMaxGames() {
		return numMaxGames;
	}

	public void setNumMaxGames(int numMaxGames) {
		this.numMaxGames = numMaxGames;
	}

}
