package com.fiap.financecontrol.domains;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    @Column(name = "valor_total", precision = 19, scale = 2, nullable = false)
    private BigDecimal valorTotal;

    private LocalDateTime dataCriacao;

    public static Vendas criar(Usuario usuario, RegistroContabil registro, BigDecimal valorTotal) {
        Vendas venda = new Vendas();
        venda.usuario = usuario;
        venda.registroContabil = registro;
        venda.valorTotal = valorTotal;

        venda.dataCriacao = LocalDateTime.now();
        return venda;
    }
}
