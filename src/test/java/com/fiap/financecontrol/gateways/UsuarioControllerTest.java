package com.fiap.financecontrol.gateways;

import com.fiap.financecontrol.domains.Usuario;
import com.fiap.financecontrol.presentation.dtos.request.ClienteRequestDto;
import com.fiap.financecontrol.presentation.UsuarioController;
import com.fiap.financecontrol.services.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
@ActiveProfiles("test")
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CreateUsuarioService createClienteService;

    @MockBean
    private UpdateUsuarioService updateClienteService;

    @MockBean
    private ListClientesService listClientesService;

    @MockBean
    private FindByIdClienteService findByIdClienteService;

    @MockBean
    private DeleteClienteService deleteClienteService;

    @Test
    void shouldCreateCliente() throws Exception {
        // Given
        ClienteRequestDto requestDto = new ClienteRequestDto();
        requestDto.setNomeCliente("João Silva");
        requestDto.setCpfCnpj("12345678901");
        requestDto.setEmail("joao@email.com");
        requestDto.setSenha("senha123");
        requestDto.setAtivo("S");

        Usuario usuario = Usuario.builder()
                .id(1L)
                .nomeCliente("João Silva")
                .cpfCnpj("12345678901")
                .email("joao@email.com")
                .senha("senha123")
                .ativo("S")
                .dataCadastro(LocalDateTime.now())
                .build();

        when(createClienteService.execute(any(Usuario.class))).thenReturn(usuario);

        // When & Then
        mockMvc.perform(post("/fiap/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nomeCliente").value("João Silva"))
                .andExpect(jsonPath("$.email").value("joao@email.com"));
    }

    @Test
    void shouldGetClienteById() throws Exception {
        // Given
        Usuario usuario = Usuario.builder()
                .id(1L)
                .nomeCliente("João Silva")
                .cpfCnpj("12345678901")
                .email("joao@email.com")
                .senha("senha123")
                .ativo("S")
                .dataCadastro(LocalDateTime.now())
                .build();

        when(findByIdClienteService.executeOrThrow(1L)).thenReturn(usuario);

        // When & Then
        mockMvc.perform(get("/fiap/clientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nomeCliente").value("João Silva"))
                .andExpect(jsonPath("$.email").value("joao@email.com"));
    }

    @Test
    void shouldReturnBadRequestForInvalidData() throws Exception {
        // Given
        ClienteRequestDto requestDto = new ClienteRequestDto();
        // Dados inválidos - campos obrigatórios vazios

        // When & Then
        mockMvc.perform(post("/fiap/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdateCliente() throws Exception {
        // Given
        ClienteRequestDto requestDto = new ClienteRequestDto();
        requestDto.setNomeCliente("João Silva Santos");
        requestDto.setCpfCnpj("12345678901");
        requestDto.setEmail("joao.santos@email.com");
        requestDto.setSenha("senha123");
        requestDto.setAtivo("S");

        Usuario usuarioAtualizado = Usuario.builder()
                .id(1L)
                .nomeCliente("João Silva Santos")
                .cpfCnpj("12345678901")
                .email("joao.santos@email.com")
                .senha("senha123")
                .ativo("S")
                .dataCadastro(LocalDateTime.now())
                .build();

        when(updateClienteService.execute(any(Usuario.class))).thenReturn(usuarioAtualizado);

        // When & Then
        mockMvc.perform(put("/fiap/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nomeCliente").value("João Silva Santos"))
                .andExpect(jsonPath("$.email").value("joao.santos@email.com"));
    }

    @Test
    void shouldDeleteCliente() throws Exception {
        // When & Then
        mockMvc.perform(delete("/fiap/clientes/1"))
                .andExpect(status().isNoContent());
    }
}
