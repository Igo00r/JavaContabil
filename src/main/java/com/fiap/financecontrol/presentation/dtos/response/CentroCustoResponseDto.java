package com.fiap.financecontrol.presentation.dtos.response;

import com.fiap.financecontrol.domains.CentroCusto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

@Data
@EqualsAndHashCode(callSuper = false)
public class CentroCustoResponseDto extends RepresentationModel<CentroCustoResponseDto> {

    private Long id;
    private String nomeCentroCusto;

    public static CentroCustoResponseDto fromEntity(CentroCusto centroCusto) {
        CentroCustoResponseDto dto = new CentroCustoResponseDto();
        dto.setId(centroCusto.getId());
        dto.setNomeCentroCusto(centroCusto.getNomeCentroCusto());
        return dto;
    }
}
