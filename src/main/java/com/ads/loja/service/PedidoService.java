
package com.ads.loja.service;

import com.ads.loja.dto.CriarPedidoDto;
import com.ads.loja.dto.ItemPedidoDto;
import com.ads.loja.model.Cliente;
import com.ads.loja.model.Pedido;
import com.ads.loja.model.Produto;
import com.ads.loja.model.ItemPedido;
import com.ads.loja.repository.ClienteRepository;
import com.ads.loja.repository.PedidoRepository;
import com.ads.loja.repository.ProdutoRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidosRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;

    public List<Pedido> buscarPedidosPorCliente(Integer clienteId) {
        return pedidosRepository.buscarPedidosPorCliente(clienteId);
    }

    public BigDecimal calcularValorTotal(Pedido pedido) {
        return pedido.getItens().stream()
                .map(item -> item.getPrecoUnitario()
                        .multiply(BigDecimal.valueOf(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional
    public Pedido criarPedido(CriarPedidoDto dados) {

        // 1. Verifica se o pedido possui itens

        if (dados.itens() == null || dados.itens().isEmpty()) {
            throw new IllegalArgumentException(
                    "O pedido deve possuir pelo menos um item"
            );
        }

        // 2. Busca o cliente

        Cliente cliente = clienteRepository.findById(dados.clienteId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Cliente não encontrado"));

        // 3. Agrupa quantidades por produto

        Map<Integer, Integer> quantidades = new TreeMap<>();

        for (ItemPedidoDto dadosItem : dados.itens()) {

            if (dadosItem.produtoId() == null) {
                throw new IllegalArgumentException(
                        "O produto deve ser informado"
                );
            }

            if (dadosItem.quantidade() == null ||
                    dadosItem.quantidade() <= 0) {
                throw new IllegalArgumentException(
                        "A quantidade deve ser maior que zero"
                );
            }

            quantidades.merge(
                    dadosItem.produtoId(),
                    dadosItem.quantidade(),
                    Math::addExact
            );
        }

        // 4. Cria o pedido

        Pedido pedido = new Pedido();

        pedido.setCliente(cliente);
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setStatus("PENDENTE");

        // 5. Verifica e desconta o estoque

        for (Map.Entry<Integer, Integer> entrada : quantidades.entrySet()) {

            Integer produtoId = entrada.getKey();
            Integer quantidadeComprada = entrada.getValue();

            Produto produto = produtoRepository
                    .buscarProdutoParaCompra(produtoId)
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Produto não encontrado: " + produtoId
                            ));

            Integer estoqueAtual = produto.getQuantidade();

            if (estoqueAtual == null || estoqueAtual < quantidadeComprada) {
                throw new IllegalArgumentException(
                        "Estoque insuficiente para o produto: "
                                + produto.getNome()
                );
            }

            // Desconta a quantidade comprada

            produto.setQuantidade(
                    estoqueAtual - quantidadeComprada
            );

            // 6. Cria o item do pedido

            ItemPedido item = new ItemPedido();

            item.setPedido(pedido);
            item.setProduto(produto);
            item.setQuantidade(quantidadeComprada);

            // Usa o preço do banco de dados

            double valor = produto.getValorPromo() > 0
                    ? produto.getValorPromo()
                    : produto.getPreco();

            BigDecimal preco = BigDecimal.valueOf(valor);

            item.setPrecoUnitario(preco);

            pedido.getItens().add(item);
        }

        // 7. Calcula o total

        pedido.setValorTotal(calcularValorTotal(pedido));

        // 8. Salva o pedido

        return pedidosRepository.save(pedido);
    }
}
