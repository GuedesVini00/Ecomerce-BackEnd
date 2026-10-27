package com.ads.loja.controller;

import com.ads.loja.dto.RecuperacaoSenhaDto;

import com.ads.loja.service.RecuperacaoSenhaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recuperacao-senha")
@RequiredArgsConstructor
public class RecuperacaoSenhaController {

    private final RecuperacaoSenhaService recuperacaoSenhaService;

    @PostMapping
    public ResponseEntity<String> recuperarSenha(
            @RequestBody RecuperacaoSenhaDto dados) {

        boolean recuperada = recuperacaoSenhaService.recuperarSenha(
                dados.email(),
                dados.novaSenha()
        );

        if (!recuperada) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok("Senha atualizada com sucesso.");
    }
}