package com.fiap.financecontrol;

import com.fiap.financecontrol.domains.events.VendaCriadaEvent;
import com.fiap.financecontrol.clients.ContaClient;
import com.fiap.financecontrol.clients.UsuarioClient;
import com.fiap.financecontrol.domains.*;
import com.fiap.financecontrol.presentation.dtos.VendasResponseDto;
import com.fiap.financecontrol.presentation.dtos.request.VendaCreateDto;
import com.fiap.financecontrol.repositories.*;

import com.fiap.financecontrol.services.conta.CreditarSaldo;
import com.fiap.financecontrol.services.usuario.RegistroContabilService;
import com.fiap.financecontrol.services.venda.VendaService;
import com.fiap.financecontrol.services.venda.VendaValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateVendasServiceTest {



    @InjectMocks
    private VendaService vendaService;
    @Mock
    private VendaValidator vendaValidator;
    @Mock
    private VendasRepository vendasRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private RegistroContabilRepository registroContabilRepository;
    @Mock
    private ContaRepository contaRepository;
    @Mock
    private CentroCustoRepository centroCustoRepository;
    @Mock
    private UsuarioClient usuarioClient;
    @Mock
    private ContaClient contaClient;
    @Mock
    private CreditarSaldo creditarSaldo;
    @Mock
    private RegistroContabilService registroContabilService;
    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private  ApplicationEventPublisher eventPublisher;

    @Test
    void deveCriarVendaComSucesso() {

        // Arrange
        Long usuarioId = 1L;
        Long contaId = 1L;
        Long centroCustoId = 1L;

        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);

        Conta conta = new Conta();
        conta.setId(contaId);
        conta.setSaldo(BigDecimal.ZERO);

        CentroCusto centro = new CentroCusto();
        centro.setId(centroCustoId);

        RegistroContabil registro = new RegistroContabil();
        registro.setConta(conta);
        registro.setCentroCusto(centro);

        Vendas venda = new Vendas();
        venda.setUsuario(usuario);
        venda.setRegistroContabil(registro);
        venda.setValorTotal(BigDecimal.TEN);


        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(contaRepository.findById(contaId)).thenReturn(Optional.of(conta));
        when(centroCustoRepository.findById(centroCustoId)).thenReturn(Optional.of(centro));

        when(vendasRepository.save(any())).thenAnswer(invocation -> {
            Vendas v = invocation.getArgument(0);
            v.setRegistroContabil(registro);
            return v;
        });


        VendaCreateDto vendaCreateDto = new VendaCreateDto(
                contaId,centroCustoId,BigDecimal.TEN
        );

        VendasResponseDto resultado = vendaService.execute(vendaCreateDto,usuarioId);


        assertNotNull(resultado);
        assertEquals(BigDecimal.ZERO, conta.getSaldo());

        verify(eventPublisher)
                .publishEvent(any(VendaCriadaEvent.class));
    }
}
