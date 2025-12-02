package com.proyecto_canal.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.CommandLineRunner;

import com.proyecto_canal.demo.modelo.UsuarioModel;
import com.proyecto_canal.demo.repositorio.UsuarioRepositorio;
import java.time.LocalDate;

@SpringBootApplication
public class CanalApplication {

	public static void main(String[] args) {
		SpringApplication.run(CanalApplication.class, args);
	}

	// Inserta un sensor de prueba al arrancar (si la tabla está vacía)
	@Bean
	public CommandLineRunner insertarSensorDePrueba(UsuarioRepositorio repo) {
		return args -> {
			try {
					
					
				while(repo.count() != 15) {
					int SenN2=((int)(Math.random()*4))+1;
					

					UsuarioModel prueba2 = new UsuarioModel();
					prueba2.setFecha(LocalDate.now());
					prueba2.setSensorId("SEN" + SenN2);
					prueba2.setNivelDeAguaCm((int)(Math.random()*140));
					prueba2.calcularTipoAlerta();
					repo.save(prueba2);
					
					System.out.println("[CanalApplication] Sensor de prueba insertado: sen "+ SenN2);
				
				}
				

			} catch (Exception ex) {
				System.err.println("[CanalApplication] Error al insertar sensores simulados: " + ex.getMessage());
			}

		};
	}

}
