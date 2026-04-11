package com.fiap.financecontrol;

import com.fiap.financecontrol.clients.ContaClient;
import com.fiap.financecontrol.clients.UsuarioClient;
import com.fiap.financecontrol.domains.*;
import com.fiap.financecontrol.presentation.dtos.response.UsuarioResponseDto;
import com.fiap.financecontrol.repositories.*;
import com.fiap.financecontrol.services.venda.CreateVendasService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateVendasServiceTest {

    @InjectMocks
    private CreateVendasService service;

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
    private RabbitTemplate rabbitTemplate;

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

        // mocks
        when(usuarioClient.buscarClientePorId(usuarioId)).thenReturn(new UsuarioResponseDto());
        when(contaClient.buscarContaPorId(contaId)).thenReturn(null);

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(contaRepository.findById(contaId)).thenReturn(Optional.of(conta));
        when(centroCustoRepository.findById(centroCustoId)).thenReturn(Optional.of(centro));

        when(registroContabilRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(vendasRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Vendas resultado = service.execute(venda);

        // Assert
        assertNotNull(resultado);
        assertEquals(BigDecimal.TEN, conta.getSaldo());

        verify(rabbitTemplate).convertAndSend(eq("fila-vendas"), contains("Venda criada"));
    }
}
