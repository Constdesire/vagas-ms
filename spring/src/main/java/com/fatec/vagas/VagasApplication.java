package com.fatec.vagas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** API de vagas: guarda o catálogo de vagas e é a ÚNICA dona do número de vagas disponíveis. */
@SpringBootApplication
public class VagasApplication {

	public static void main(String[] args) {
		SpringApplication.run(VagasApplication.class, args);
	}

}
