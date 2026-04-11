package com.fiap.financecontrol.services.registroContabil;


import com.fiap.financecontrol.domains.RegistroContabil;
import com.fiap.financecontrol.repositories.RegistroContabilRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class FindByIdRegistroContabilService {

    private final RegistroContabilRepository registroContabilRepository;

    public Optional<RegistroContabil> execute(Long id) {
        return registroContabilRepository.findById(id);
    }

    public RegistroContabil executeOrThrow(Long id) {
        return registroContabilRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro Contábil não encontrado com ID: " + id));
    }
}