# To-Do API

Backend REST para gerenciamento de tarefas, desenvolvido como projeto avaliativo da disciplina de Laboratorio de Desenvolvimento Multiplataforma do 6º semestre de DSM.

## Requisitos atendidos

- Cadastro, consulta, atualização e exclusão de tarefas.
- Entidade com nome, descrição, status, observações, data de criação e data de atualização.
- Datas preenchidas automaticamente pela aplicação.
- API sem sistema de login e sem conceito de usuário da aplicação.
- Persistência em PostgreSQL com scripts de criação do banco e das tabelas.
- Camadas de modelo, repositório, serviço e controlador.
- Validação dos dados e respostas de erro padronizadas.
- Testes unitários e testes de integração.
- Interface web responsiva para usar todas as operações do CRUD.

## Tecnologias

- Java 17
- Spring Boot 4.1.1
- Spring Web MVC
- Spring Data JPA / Hibernate
- PostgreSQL
- Bean Validation
- JUnit 5, Mockito, MockMvc e H2 (testes)
- Maven Wrapper

## Modelo de tarefa

| Campo | Tipo | Regra |
| --- | --- | --- |
| `id` | Long | Gerado automaticamente |
| `nome` | String | Obrigatório, máximo de 120 caracteres |
| `descricao` | String | Obrigatória, máximo de 1000 caracteres |
| `status` | Enum | `PENDENTE`, `EM_ANDAMENTO` ou `CONCLUIDA` |
| `observacoes` | String | Opcional, máximo de 2000 caracteres |
| `dataCriacao` | Data/hora | Preenchida automaticamente em UTC |
| `dataAtualizacao` | Data/hora | Atualizada automaticamente em UTC |

## Como executar

### 1. Preparar o banco de dados

Execute os scripts na ordem indicada:

```powershell
psql -U postgres -f database/create_database.sql
psql -U postgres -d todo_db -f database/schema.sql
```

Se o banco `todo_db` já existir, execute somente o segundo comando.

### 2. Configurar a conexão

Informe as credenciais da sua instalação local do PostgreSQL. Nenhuma senha real é armazenada no repositório:

```powershell
$env:DB_PASSWORD="<sua-senha>"
$env:DB_URL="jdbc:postgresql://localhost:5432/todo_db" # opcional
$env:DB_USERNAME="gabriel"                             # opcional
```

O usuário padrão é `gabriel`. Caso ele não exista em outra instalação, defina `DB_USERNAME` com um usuário PostgreSQL que possua acesso ao banco.

### 3. Iniciar a aplicação

```powershell
.\mvnw.cmd spring-boot:run
```

A interface estará disponível em `http://localhost:8080` e consumirá a API automaticamente.

## Interface web

A tela permite cadastrar, consultar, editar, concluir, reabrir e excluir tarefas. Também inclui busca por texto, filtros por status e contadores de acompanhamento. Foi implementada com HTML, CSS e JavaScript puros e é servida pelo próprio Spring Boot, sem dependências adicionais.

A API permanece disponível em `http://localhost:8080/api/tarefas`.

## Endpoints

| Método | Rota | Descrição |
| --- | --- | --- |
| `GET` | `/api/tarefas` | Lista todas as tarefas |
| `GET` | `/api/tarefas/{id}` | Busca uma tarefa |
| `POST` | `/api/tarefas` | Cria uma tarefa |
| `PUT` | `/api/tarefas/{id}` | Atualiza uma tarefa |
| `DELETE` | `/api/tarefas/{id}` | Exclui uma tarefa |

Exemplo de corpo para `POST` e `PUT`:

```json
{
  "nome": "Estudar Spring Boot",
  "descricao": "Revisar os conceitos de API REST",
  "status": "PENDENTE",
  "observacoes": "Separar duas horas"
}
```

## Executar os testes

Os testes usam um banco H2 temporário em modo de compatibilidade com PostgreSQL; portanto, não exigem um banco externo em execução.

```powershell
.\mvnw.cmd test
```

O projeto inclui testes unitários da camada de serviço, testes de integração do fluxo HTTP completo e validação da inicialização do contexto Spring.
