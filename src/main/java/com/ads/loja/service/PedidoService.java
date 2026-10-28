
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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

    public Pedido criarPedido(CriarPedidoDto dados) {
        Cliente cliente = clienteRepository.findById(dados.clienteId())
                .orElseThrow(() ->
                        new RuntimeException("Cliente não encontrado"));

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setStatus("PENDENTE");

        if (dados.itens() == null || dados.itens().isEmpty()) {
            throw new IllegalArgumentException(
                    "O pedido deve possuir pelo menos um item"
            );
        }

        for (ItemPedidoDto dadosItem : dados.itens()) {
            if (dadosItem.quantidade() == null ||
                    dadosItem.quantidade() <= 0) {
                throw new IllegalArgumentException(
                        "A quantidade deve ser maior que zero");
            }

            Produto produto = produtoRepository.findById(
                    dadosItem.produtoId()
            ).orElseThrow(() ->
                    new RuntimeException("Produto não encontrado"));

            ItemPedido item = new ItemPedido();
            item.setPedido(pedido);
            item.setProduto(produto);
            item.setQuantidade(dadosItem.quantidade());

            // Usa o preço cadastrado no banco.
            BigDecimal preco = BigDecimal.valueOf(
                    produto.getValorPromo() > 0
                            ? produto.getValorPromo()
                            : produto.getValor()
            );

            item.setPrecoUnitario(preco);
            pedido.getItens().add(item);
        }

        pedido.setValorTotal(calcularValorTotal(pedido));

        return pedidosRepository.save(pedido);
    }

}
