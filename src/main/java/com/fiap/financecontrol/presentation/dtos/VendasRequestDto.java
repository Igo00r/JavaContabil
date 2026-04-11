package com.fiap.financecontrol.presentation.dtos;

import com.fiap.financecontrol.domains.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class VendasRequestDto {



    @NotNull(message = "O valor da venda é obrigatório")
    private BigDecimal valorTotal;

    @NotNull(message = "ID da conta é obrigatório")
    private Long contaId;

    @NotNull(message = "ID do centro de custo é obrigatório")
    private Long centroCustoId;

    public Vendas toEntity() {
        // Criamos a estrutura básica que o Service vai completar
        Vendas venda = new Vendas();
        venda.setValorTotal(this.valorTotal);

        // Criamos objetos "Dummies" apenas com ID para o Service carregar do banco
        Usuario usuario = new Usuario();

        venda.setUsuario(usuario);

        // Preparamos o RegistroContabil com o que o Service precisa para processar
        RegistroContabil reg = new RegistroContabil();

        Conta conta = new Conta();
        conta.setId(this.contaId);
        reg.setConta(conta);

        CentroCusto cc = new CentroCusto();
        cc.setId(this.centroCustoId);
        reg.setCentroCusto(cc);

        venda.setRegistroContabil(reg);

        return venda;
    }
}