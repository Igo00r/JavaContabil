package com.fiap.financecontrol.services;

import com.fiap.financecontrol.domains.Usuario;
import com.fiap.financecontrol.domains.RegistroContabil;
import com.fiap.financecontrol.domains.Vendas;
import com.fiap.financecontrol.gateways.UsuarioRepository;
import com.fiap.financecontrol.gateways.RegistroContabilRepository;
import com.fiap.financecontrol.gateways.VendasRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateVendasService implements VendasDataServiceInterface {

    private final VendasRepository vendasRepository;
    private final UsuarioRepository usuarioRepository;
    private final RegistroContabilRepository registroContabilRepository;

    @Override
    @Transactional
    public Vendas execute(Vendas vendas) {
        if (!vendasRepository.existsById(vendas.getId())) {
            throw new RuntimeException("Venda não encontrada com ID: " + vendas.getId());
        }

        // Validar e carregar usuario
        Usuario usuario = usuarioRepository.findById(vendas.getUsuario().getId())
                .orElseThrow(() -> new RuntimeException("Usuario não encontrado com ID: " + vendas.getUsuario().getId()));
        vendas.setUsuario(usuario);

        // Validar e carregar registro contábil
        RegistroContabil registroContabil = registroContabilRepository.findById(vendas.getRegistroContabil().getId())
                .orElseThrow(() -> new RuntimeException("Registro Contábil não encontrado com ID: " + vendas.getRegistroContabil().getId()));
        vendas.setRegistroContabil(registroContabil);

        return vendasRepository.save(vendas);
    }
}
