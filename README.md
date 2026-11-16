<div align="center">

# MATRIX | E-commerce API

**Backend de uma plataforma de e-commerce desenvolvido com Java, Spring Boot e PostgreSQL.**

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.1.1-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Neon-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-Build-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)

[Frontend Angular](https://github.com/GuedesVini00/Ecomerce-FrontEnd)

</div>

## Sobre o projeto

A **MATRIX** é um projeto acadêmico de e-commerce que integra uma interface Angular a uma API REST em Spring Boot. O backend centraliza as operações de clientes, produtos, carrinho e pedidos, aplica regras de negócio e persiste as informações em um banco **PostgreSQL hospedado no Neon**.

O projeto foi desenvolvido para praticar **Java, arquitetura em camadas, APIs REST, persistência com JPA/Hibernate e integração frontend–backend**.

## Tecnologias

- **Java 17** e **Spring Boot 4.1.1** — desenvolvimento da API REST.
- **Spring Web MVC** — endpoints HTTP.
- **Spring Data JPA / Hibernate** — mapeamento de entidades e persistência.
- **PostgreSQL / Neon** — banco de dados relacional em nuvem.
- **Maven / Maven Wrapper** — gerenciamento de dependências e execução.
- **Lombok** — redução de código repetitivo.
- **Bean Validation** — validação de dados em endpoints que a utilizam.
- **BCrypt (Spring Security Crypto)** — hash de senhas.

> O projeto usa BCrypt para senhas, mas **não implementa autenticação JWT**.

## Funcionalidades

- Cadastro, consulta, atualização e exclusão de clientes.
- Login com verificação de senha protegida por BCrypt.
- Recuperação de senha por código temporário **simulado no terminal** (não envia e-mail).
- Cadastro, listagem, consulta, atualização e exclusão de produtos.
- Carrinho persistido no banco, com adição, alteração de quantidade e remoção de itens.
- Consideração do preço promocional dos produtos.
- Criação de pedidos vinculados a clientes e produtos.
- Verificação de estoque antes da compra e atualização das quantidades disponíveis.
- Cálculo do total do pedido com desconto e frete informados na requisição.
- Limpeza do carrinho após a criação bem-sucedida do pedido.
- Histórico de pedidos por cliente, com itens, quantidades, valores e status.

> **Observação:** o desconto e o frete são recebidos do frontend na versão analisada. Em uma evolução do projeto, esses valores devem ser calculados ou validados pelo backend. As formas de pagamento exibidas na interface são demonstrativas: não existe integração com gateway de pagamento.

## Arquitetura

A aplicação está organizada em camadas:

```text
Angular (frontend)
       |
       | HTTP / JSON
       v
Controller  ->  Service  ->  Repository  ->  PostgreSQL (Neon)
                 |
                 +-- Regras de negócio e validações
```

- **Controller:** recebe requisições e devolve respostas HTTP.
- **Service:** concentra regras de negócio e operações da aplicação.
- **Repository:** acessa os dados utilizando Spring Data JPA.
- **Model / Entity:** representa os dados persistidos.
- **DTO:** define estruturas de dados utilizadas nas requisições e respostas.

### Organização de pastas

```text
src/main/java/com/ads/loja/
├── config/
├── controller/
├── dto/
├── exception/
├── model/
├── repository/
└── service/

src/main/resources/
└── application.properties
```

## Endpoints principais

**URL base local:** `http://localhost:8080`

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/api/clientes` | Cadastrar cliente |
| `GET` | `/api/clientes` | Listar clientes |
| `GET` | `/api/clientes/{id}` | Buscar cliente |
| `PUT` | `/api/clientes/{id}` | Atualizar cliente |
| `DELETE` | `/api/clientes/{id}` | Excluir cliente |
| `POST` | `/api/login` | Realizar login |
| `POST` | `/api/recuperacao-senha/solicitar` | Solicitar código de recuperação |
| `POST` | `/api/recuperacao-senha/redefinir` | Redefinir senha com código |
| `GET` | `/api/produtos` | Listar produtos |
| `GET` | `/api/produtos/{id}` | Consultar produto |
| `POST` | `/api/produtos` | Cadastrar produto |
| `PUT` | `/api/produtos/{id}` | Atualizar produto |
| `DELETE` | `/api/produtos/{id}` | Excluir produto |
| `GET` | `/api/carrinho/{clienteId}` | Consultar carrinho |
| `POST` | `/api/carrinho/{clienteId}/itens` | Adicionar produto ao carrinho |
| `PUT` | `/api/carrinho/{clienteId}/itens/{produtoId}` | Alterar quantidade |
| `DELETE` | `/api/carrinho/{clienteId}/itens/{produtoId}` | Remover produto |
| `POST` | `/api/pedidos` | Criar pedido |
| `GET` | `/api/pedidos/cliente/{clienteId}` | Consultar histórico do cliente |

### Exemplo: criar um pedido

`POST /api/pedidos`

```json
{
  "clienteId": 1,
  "itens": [
    { "produtoId": 2, "quantidade": 2 }
  ],
  "desconto": 10.00,
  "frete": 19.90
}
```

Os IDs devem corresponder a registros existentes e o estoque deve ser suficiente. O backend calcula o valor dos produtos com base nos dados cadastrados e considera os campos de desconto e frete recebidos.

## Como executar localmente

### Pré-requisitos

- **JDK 17** instalado.
- Acesso a uma instância PostgreSQL (por exemplo, no Neon).
- Git (para clonar o repositório).
- Maven não é obrigatório, pois o projeto inclui **Maven Wrapper**.

### 1. Clone o repositório

```powershell
git clone https://github.com/GuedesVini00/Ecomerce-BackEnd.git
cd Ecomerce-BackEnd
```

### 2. Configure o banco

Na raiz do backend, crie um arquivo `.env` com base em `.env.example`:

```properties
DB_URL=jdbc:postgresql://SEU_HOST/neondb?sslmode=require
DB_USERNAME=SEU_USUARIO
DB_PASSWORD=SUA_SENHA
```

O arquivo `application.properties` utiliza essas variáveis para configurar a conexão. **Não publique o `.env` nem credenciais reais no GitHub.**

### 3. Execute o backend (Windows / PowerShell)

```powershell
.\iniciar.ps1
```

O script presente no projeto carrega o `.env` e executa o Maven Wrapper. Se a política de execução do PowerShell bloquear scripts, ajuste-a apenas conforme as regras de segurança do seu ambiente.

Alternativamente, com as variáveis de ambiente já configuradas:

```powershell
.\mvnw.cmd spring-boot:run
```

A API utiliza, por padrão, a porta **8080**.

### 4. Teste a API

Abra no navegador:

```text
http://localhost:8080/api/produtos
```

Ou utilize Postman/Insomnia para testar os demais endpoints.

## Integração com o frontend

O frontend da MATRIX foi desenvolvido em **Angular** e consome os endpoints desta API via HTTP, com dados JSON. No ambiente local, o frontend costuma executar em `http://localhost:4200`.

Repositório: **[Ecomerce-FrontEnd](https://github.com/GuedesVini00/Ecomerce-FrontEnd)**.

## Observações sobre segurança e escopo

Este é um **projeto acadêmico**, não uma aplicação pronta para produção. Entre as melhorias futuras estão autenticação e autorização robustas, validação de cupons e frete exclusivamente no servidor, integração com pagamentos reais e envio de códigos de recuperação por e-mail.

As credenciais do banco devem ser compartilhadas por meio seguro, nunca por commits, arquivos públicos ou capturas de tela.

---

<div align="center">

**MATRIX — Projeto acadêmico de desenvolvimento de sistemas**

</div>
