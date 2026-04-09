package com.fiap.financecontrol.presentation.dtos;

import com.fiap.financecontrol.domains.Vendas;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
public class VendasResponseDto extends RepresentationModel<VendasResponseDto> {

    private Long id;
    private Long clienteId;
    private String nomeCliente;
    private String emailCliente;
    private Long registroContabilId;
    private BigDecimal valorRegistro;
    private String nomeConta;
    private String nomeCentroCusto;

    public static VendasResponseDto fromEntity(Vendas venda) {
        VendasResponseDto dto = new VendasResponseDto();
        dto.setId(venda.getId());
        dto.setClienteId(venda.getUsuario().getId());
        dto.setNomeCliente(venda.getUsuario().getNome());
        dto.setEmailCliente(venda.getUsuario().getEmail());
        dto.setRegistroContabilId(venda.getRegistroContabil().getId());
        dto.setValorRegistro(venda.getRegistroContabil().getValor());
        dto.setNomeConta(venda.getRegistroContabil().getConta().getNomeConta());
        dto.setNomeCentroCusto(venda.getRegistroContabil().getCentroCusto().getNomeCentroCusto());
        return dto;
    }
}
