package com.ads.loja.dto;

import java.util.List;

public record CriarPedidoDto(
        Integer clienteId,
        List<ItemPedidoDto> itens
) {}