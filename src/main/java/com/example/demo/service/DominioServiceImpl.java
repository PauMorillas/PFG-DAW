package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.model.dto.DominioDTO;

@Service
public class DominioServiceImpl implements DominioService {

    @Override
    public List<DominioDTO> getDominios() {
        // TODO: Implementación de los métodos definidos en la interfaz DominioService

        return List.of(
            new DominioDTO() {{ setId(1L); setNombre("Dominio 1"); }},
            new DominioDTO() {{ setId(2L); setNombre("Dominio 2"); }}
        );
    }
    
}
