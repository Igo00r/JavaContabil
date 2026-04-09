package com.fiap.financecontrol.presentation.dtos.request;

import com.fiap.financecontrol.domains.RegistroContabil;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RegistroContabilRequestDto {

    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    private BigDecimal valor;

    @NotNull(message = "ID da conta é obrigatório")
    private Long contaId;

    @NotNull(message = "ID do centro de custo é obrigatório")
    private Long centroCustoId;

    public RegistroContabil toEntity() {
        return RegistroContabil.builder()
                .valor(this.valor)
                .build();
    }
}
