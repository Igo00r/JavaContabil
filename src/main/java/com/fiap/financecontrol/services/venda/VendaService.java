package com.fiap.financecontrol.services.venda;

import com.fiap.financecontrol.event.events.VendaCriadaEvent;
import com.fiap.financecontrol.domains.*;
import com.fiap.financecontrol.exceptions.CentroCustoNaoEncontradoException;
import com.fiap.financecontrol.exceptions.ContaFinanceiraNaoEncontradaException;
import com.fiap.financecontrol.exceptions.UsuarioNaoEncontradoException;
import com.fiap.financecontrol.presentation.dtos.VendasResponseDto;
import com.fiap.financecontrol.presentation.dtos.request.VendaCreateDto;
import com.fiap.financecontrol.repositories.CentroCustoRepository;
import com.fiap.financecontrol.repositories.ContaRepository;
import com.fiap.financecontrol.repositories.UsuarioRepository;
import com.fiap.financecontrol.repositories.VendasRepository;
import com.fiap.financecontrol.services.conta.CreditarSaldo;
import com.fiap.financecontrol.services.usuario.RegistroContabilService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class VendaService {
    private final VendaValidator validator;


    private final VendasRepository vendasRepository;
    private final CreditarSaldo creditarSaldo;
    private final RegistroContabilService registroContabilService;
    private final UsuarioRepository usuarioRepository;
    private final ContaRepository contaRepository;
    private final CentroCustoRepository centroCustoRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public VendasResponseDto execute(VendaCreateDto dto, Long userId) {
        log.info("Iniciando execução da venda...");
       validator.validar(dto,userId);
       log.info("Validação OK, buscando usuário no banco...");

        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado"));
        Conta conta = contaRepository.findById(dto.contaId()).orElseThrow(()->new ContaFinanceiraNaoEncontradaException("Conta inexistente"));
        CentroCusto centroCusto = centroCustoRepository.findById(dto.centroCustoId()).orElseThrow(()->new CentroCustoNaoEncontradoException("Centro custo inexistente"));

        creditarSaldo.creditar(dto.contaId(), dto.valorTotal());
        RegistroContabil registro = registroContabilService.execute(conta, centroCusto, dto.valorTotal());

        Vendas vendaSalva = vendasRepository.save(Vendas.criar(usuario, registro, dto.valorTotal()));

        eventPublisher.publishEvent(new VendaCriadaEvent(vendaSalva));

        return VendasResponseDto.fromEntity(vendaSalva);
    }
}
