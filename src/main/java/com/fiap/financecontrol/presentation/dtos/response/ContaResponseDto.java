package com.fiap.financecontrol.presentation.dtos.response;

import com.fiap.financecontrol.domains.Conta;
import com.fiap.financecontrol.domains.TipoConta;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

@Data
@EqualsAndHashCode(callSuper = false)
public class ContaResponseDto extends RepresentationModel<ContaResponseDto> {

    private Long id;
    private String nomeConta;
    private TipoConta tipo;
    private Long usuarioId;
    private String nomeCliente;

    public static ContaResponseDto fromEntity(Conta conta) {
        ContaResponseDto dto = new ContaResponseDto();
        dto.setId(conta.getId());
        dto.setNomeConta(conta.getNomeConta());
        dto.setTipo(conta.getTipo());
        dto.setUsuarioId(conta.getUsuario() != null ? conta.getUsuario().getId() : null);
        dto.setNomeCliente(conta.getUsuario() != null ? conta.getUsuario().getNome() : null);
        return dto;
    }
}
