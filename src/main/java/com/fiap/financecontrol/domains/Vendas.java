package com.fiap.financecontrol.domains;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "VENDAS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Vendas {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vendas_seq")
    @SequenceGenerator(name = "vendas_seq", sequenceName = "vendas_seq", allocationSize = 1)
    @Column(name = "id_vendas")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id_usuario", nullable = false)
    private Usuario usuario;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reg_cont_id_reg_cont", nullable = false)
    private RegistroContabil registroContabil;

    @NotNull(message = "O valor total da venda não pode ser nulo")
    @Column(name = "valor_total", precision = 19, scale = 2, nullable = false)
    private BigDecimal valorTotal;
}
