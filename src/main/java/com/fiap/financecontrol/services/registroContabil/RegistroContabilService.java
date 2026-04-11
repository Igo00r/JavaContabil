package com.fiap.financecontrol.services.registroContabil;

import com.fiap.financecontrol.domains.RegistroContabil;
import com.fiap.financecontrol.repositories.RegistroContabilRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegistroContabilService {

    private final RegistroContabilRepository registroContabilRepository;


    @Transactional
    public void deleteRegistro(Long id) {
        if (!registroContabilRepository.existsById(id)) {
            throw new RuntimeException("Registro Contábil não encontrado com ID: " + id);
        }
        registroContabilRepository.deleteById(id);
    }

    public Page<RegistroContabil> listarRegistrosContabeis(int page, int size, Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "dataCriacao"));
        return registroContabilRepository.findAll(pageable);
    }

    public Page<RegistroContabil> listarRegistrosPorConta(Long contaId, int page, int size, Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "dataCriacao"));
        return registroContabilRepository.findByContaId(contaId, pageable);
    }

    public Page<RegistroContabil> listarRegistrosPorCentroCusto(Long centroCustoId, int page, int size, Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "dataCriacao"));
        return registroContabilRepository.findByCentroCustoId(centroCustoId, pageable);
    }

    public Page<RegistroContabil> listarRegistrosPorValor(BigDecimal valorMinimo, BigDecimal valorMaximo, int page, int size, Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "dataCriacao"));
        return registroContabilRepository.findByValorBetween(valorMinimo, valorMaximo, pageable);
    }

    public Optional<RegistroContabil> execute(Long id) {
        return registroContabilRepository.findById(id);
    }

    public RegistroContabil executeOrThrow(Long id) {
        return registroContabilRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro Contábil não encontrado com ID: " + id));
    }

}
