package com.fiap.financecontrol.services.centroCusto;
import com.fiap.financecontrol.domains.CentroCusto;
import com.fiap.financecontrol.repositories.CentroCustoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@RequiredArgsConstructor
public class CreateCentroCustoService implements CentroCustoDataServiceInterface {

    private final CentroCustoRepository centroCustoRepository;

    @Override
    @Transactional
    public CentroCusto execute(CentroCusto centroCusto) {
        return centroCustoRepository.save(centroCusto);
    }
}