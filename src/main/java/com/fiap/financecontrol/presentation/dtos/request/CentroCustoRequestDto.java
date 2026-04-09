package com.fiap.financecontrol.presentation.dtos.request;

import com.fiap.financecontrol.domains.CentroCusto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CentroCustoRequestDto {

    @NotBlank(message = "Nome do centro de custo é obrigatório")
    @Size(max = 70, message = "Nome do centro de custo deve ter no máximo 70 caracteres")
    private String nomeCentroCusto;

    public CentroCusto toEntity() {
        return CentroCusto.builder()
                .nomeCentroCusto(this.nomeCentroCusto)
                .build();
    }
}
