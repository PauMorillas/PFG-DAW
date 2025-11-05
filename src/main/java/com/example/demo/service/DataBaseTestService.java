package com.example.demo.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.repository.dao.GerenteRepository;
import com.example.demo.repository.entity.Gerente;

public class DataBaseTestService {
    @Autowired
    private GerenteRepository gerenteRepository;

    /**
     * Intenta buscar el primer registro de Gerente.
     * @return String con el resultado de la conexión.
     */
    public String testConnection() {
        try {
            // Intentar buscar el Gerente con ID 1 (sabemos que existe en 02_data.sql)
            Optional<Gerente> gerente = gerenteRepository.findById(1L);

            if (gerente.isPresent()) {
                return "¡Conexión a BD y JPA exitosa! Encontrado Gerente: " + gerente.get().getNombre();
            } else {
                return "¡Conexión a BD exitosa, pero no se encontró el Gerente con ID 1! (Revisa 02_data.sql)";
            }
        } catch (Exception e) {
            // Captura errores de conexión (URL, credenciales, etc.)
            return "¡Fallo crítico al conectar o consultar la BD! Causa: " + e.getMessage();
        }
    }
}
