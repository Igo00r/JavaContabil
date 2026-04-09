package com.fiap.financecontrol.gateways.dtos;

import com.fiap.financecontrol.domains.Vendas;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VendasRequestDto {

    @NotNull(message = "ID do usuario é obrigatório")
    private Long clienteId;

    @NotNull(message = "ID do registro contábil é obrigatório")
    private Long registroContabilId;

    public Vendas toEntity() {
        return Vendas.builder()
                .build();
    }
}
