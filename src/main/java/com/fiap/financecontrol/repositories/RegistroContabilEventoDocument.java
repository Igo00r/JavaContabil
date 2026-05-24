package com.fiap.financecontrol.repositories;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(collection = "venda_eventos")
@Getter
@Setter

public class RegistroContabilEventoDocument {
    @Id
    private String id;

    private BigDecimal valor;

    private Long contaId;

    private Long centroCustoId;

    private LocalDateTime dataCriacao;

    private LocalDateTime dataAtualizacao;
}
