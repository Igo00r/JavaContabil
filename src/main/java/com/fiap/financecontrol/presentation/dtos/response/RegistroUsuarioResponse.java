package com.fiap.financecontrol.presentation.dtos.response;

import java.time.LocalDateTime;

public record RegistroUsuarioResponse (Long id, String email, String nome, LocalDateTime criadoEm ) {
}
