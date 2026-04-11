package com.fiap.financecontrol.presentation.dtos.request;

import com.fiap.financecontrol.domains.Usuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UsuarioRequestDto {

    @NotBlank(message = "Nome do usuario é obrigatório")
    @Size(max = 100, message = "Nome do usuario deve ter no máximo 100 caracteres")
    private String nomeCliente;

    @NotBlank(message = "CPF/CNPJ é obrigatório")
    @Size(max = 14, message = "CPF/CNPJ deve ter no máximo 14 caracteres")
    @Pattern(regexp = "\\d{11}|\\d{14}", message = "CPF deve ter 11 dígitos ou CNPJ deve ter 14 dígitos")
    private String cpfCnpj;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ter formato válido")
    @Size(max = 100, message = "Email deve ter no máximo 100 caracteres")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, max = 100, message = "Senha deve ter entre 6 e 100 caracteres")
    private String senha;

    @Pattern(regexp = "[SN]", message = "Status ativo deve ser 'S' ou 'N'")
    private String ativo = "S";

    public Usuario toEntity() {
        return Usuario.builder()
                .nome(this.nomeCliente)
                .cpfCnpj(this.cpfCnpj)
                .email(this.email)
                .senha(this.senha)
                .ativo(this.ativo)
                .build();
    }
}
