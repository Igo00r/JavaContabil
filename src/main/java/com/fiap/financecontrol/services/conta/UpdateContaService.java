package com.fiap.financecontrol.services.conta;

import com.fiap.financecontrol.domains.Usuario;
import com.fiap.financecontrol.domains.Conta;
import com.fiap.financecontrol.repositories.UsuarioRepository;
import com.fiap.financecontrol.repositories.ContaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateContaService implements ContaDataServiceInterface {

    private final ContaRepository contaRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public Conta execute(Conta conta) {
        if (!contaRepository.existsById(conta.getId())) {
            throw new RuntimeException("Conta não encontrada com ID: " + conta.getId());
        }
        
        if (conta.getUsuario() != null && conta.getUsuario().getId() != null) {
            Usuario usuario = usuarioRepository.findById(conta.getUsuario().getId())
                    .orElseThrow(() -> new RuntimeException("Usuario não encontrado com ID: " + conta.getUsuario().getId()));
            conta.setUsuario(usuario);
        }
        
        return contaRepository.save(conta);
    }
}
