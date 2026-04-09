package com.fiap.financecontrol.presentation.dtos.request;

import com.fiap.financecontrol.domains.Conta;
import com.fiap.financecontrol.domains.TipoConta;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ContaRequestDto {

    @NotBlank(message = "Nome da conta é obrigatório")
    @Size(max = 70, message = "Nome da conta deve ter no máximo 70 caracteres")
    private String nomeConta;

    @NotNull(message = "Tipo da conta é obrigatório")
    private TipoConta tipo;

    private Long clienteId;

    public Conta toEntity() {
        return Conta.builder()
                .nomeConta(this.nomeConta)
                .tipo(this.tipo)
                .build();
    }
}
