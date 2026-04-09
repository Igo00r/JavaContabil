package com.fiap.financecontrol.presentation.dtos.response;

import com.fiap.financecontrol.domains.RegistroContabil;
import com.fiap.financecontrol.domains.TipoConta;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
public class RegistroContabilResponseDto extends RepresentationModel<RegistroContabilResponseDto> {

    private Long id;
    private BigDecimal valor;
    private Long contaId;
    private String nomeConta;
    private TipoConta tipoConta;
    private Long centroCustoId;
    private String nomeCentroCusto;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    public static RegistroContabilResponseDto fromEntity(RegistroContabil registro) {
        RegistroContabilResponseDto dto = new RegistroContabilResponseDto();
        dto.setId(registro.getId());
        dto.setValor(registro.getValor());
        dto.setContaId(registro.getConta().getId());
        dto.setNomeConta(registro.getConta().getNomeConta());
        dto.setTipoConta(registro.getConta().getTipo());
        dto.setCentroCustoId(registro.getCentroCusto().getId());
        dto.setNomeCentroCusto(registro.getCentroCusto().getNomeCentroCusto());
        dto.setDataCriacao(registro.getDataCriacao());
        dto.setDataAtualizacao(registro.getDataAtualizacao());
        return dto;
    }
}