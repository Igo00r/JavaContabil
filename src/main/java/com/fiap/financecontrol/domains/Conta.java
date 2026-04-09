package com.fiap.financecontrol.domains;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "CONTA_CONTABIL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "conta_seq")
    @SequenceGenerator(name = "conta_seq", sequenceName = "conta_seq", allocationSize = 1)
    @Column(name = "id_conta_contabil")
    private Long id;

    @NotBlank
    @Size(max = 70)
    @Column(name = "nome_conta_contabil", nullable = false, length = 70)
    private String nomeConta;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 1)
    private TipoConta tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id_usuario", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "conta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<RegistroContabil> registrosContabeis = new ArrayList<>();

    public void adicionarRegistroContabil(RegistroContabil registro) {
        registrosContabeis.add(registro);
        registro.setConta(this);
    }
}
