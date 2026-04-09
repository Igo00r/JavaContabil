package com.fiap.financecontrol.gateways.dtos;

import com.fiap.financecontrol.domains.Usuario;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
public class ClienteResponseDto extends RepresentationModel<ClienteResponseDto> {

    private Long id;
    private String nomeCliente;
    private LocalDateTime dataCadastro;
    private String cpfCnpj;
    private String email;
    private String ativo;

    public static ClienteResponseDto fromEntity(Usuario usuario) {
        ClienteResponseDto dto = new ClienteResponseDto();
        dto.setId(usuario.getId());
        dto.setNomeCliente(usuario.getNome());
        dto.setDataCadastro(usuario.getDataCadastro());
        dto.setCpfCnpj(usuario.getCpfCnpj());
        dto.setEmail(usuario.getEmail());
        dto.setAtivo(usuario.getAtivo());
        return dto;
    }
}
