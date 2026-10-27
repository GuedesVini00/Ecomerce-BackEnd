package com.ads.loja.service;

import com.ads.loja.model.Cliente;
import com.ads.loja.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecuperacaoSenhaService {

    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;

    public boolean recuperarSenha(String email, String novaSenha) {
        Optional<Cliente> clienteEncontrado =
                clienteRepository.findByEmail(email);

        if (clienteEncontrado.isEmpty()) {
            return false;
        }

        Cliente cliente = clienteEncontrado.get();

        cliente.setSenha(passwordEncoder.encode(novaSenha));

        clienteRepository.save(cliente);

        return true;
    }
}
