package com.fiap.financecontrol.services.registroContabil;

import com.fiap.financecontrol.repositories.RegistroContabilRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteRegistroContabilService {

    private final RegistroContabilRepository registroContabilRepository;

    @Transactional
    public void execute(Long id) {
        if (!registroContabilRepository.existsById(id)) {
            throw new RuntimeException("Registro Contábil não encontrado com ID: " + id);
        }
        registroContabilRepository.deleteById(id);
    }
}