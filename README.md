# ☑️ KeepTheFlow

### Organize suas tarefas. Priorize o que importa. Mantenha o fluxo.

Uma aplicação full stack de gerenciamento de tarefas com **Java, Spring Boot, Spring Security e JWT**, desenvolvida com uma interface Kanban interativa, autenticação de usuários e persistência de dados em PostgreSQL.

🔗 **[Acessar aplicação online](https://keeptheflow.onrender.com/)**

---

## 🛠️ Tecnologias

**Backend**

![Java](https://img.shields.io/badge/Java-25-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.1-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)
![Hibernate](https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=hibernate&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)

**Banco de dados e persistência**

![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![H2](https://img.shields.io/badge/H2%20Database-007396?style=for-the-badge&logo=databricks&logoColor=white)
![Flyway](https://img.shields.io/badge/Flyway-CC0200?style=for-the-badge&logo=flyway&logoColor=white)

**Frontend**

![HTML5](https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=html5&logoColor=white)
![CSS3](https://img.shields.io/badge/CSS3-1572B6?style=for-the-badge&logo=css3&logoColor=white)
![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black)
![Bootstrap](https://img.shields.io/badge/Bootstrap-7952B3?style=for-the-badge&logo=bootstrap&logoColor=white)

**Testes e infraestrutura**

![JUnit 5](https://img.shields.io/badge/JUnit%205-25A162?style=for-the-badge&logo=junit5&logoColor=white)
![Mockito](https://img.shields.io/badge/Mockito-78A641?style=for-the-badge&logo=mockito&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Render](https://img.shields.io/badge/Render-000000?style=for-the-badge&logo=render&logoColor=white)
![Git](https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white)

---

## 📋 Funcionalidades principais

### 🔐 Autenticação e segurança
- Cadastro e login de usuários.
- Autenticação baseada em **JWT (JSON Web Token)**.
- Proteção de endpoints com **Spring Security**.
- Senhas gerenciadas com mecanismos seguros de codificação.
- Isolamento das tarefas por usuário autenticado.
- Consulta e atualização dos dados do perfil.

### 📋 Gerenciamento de tarefas
- Criação, consulta, atualização e exclusão de tarefas — operações CRUD.
- Organização em quadro Kanban com colunas de tarefas pendentes e concluídas.
- Definição de prioridades.
- Pesquisa de tarefas.
- Indicadores de total de tarefas, pendentes e concluídas.

### 🖱️ Interface interativa
- **Drag and drop:** movimentação de tarefas entre as colunas do Kanban.
- Atualização do estado das tarefas no backend.
- Modal de confirmação antes da exclusão.
- Interface responsiva e navegação adaptada a dispositivos móveis.

---

## 🧪 Testes automatizados

O projeto possui testes unitários e de integração para validar as regras de negócio, a API REST e os mecanismos de segurança.

### Testes de integração

**Tecnologias:** JUnit 5, Spring Boot Test e MockMvc.

Os testes simulam requisições HTTP e verificam respostas, dados retornados e regras de acesso. O banco **H2 em memória** é utilizado no ambiente de testes, separado do PostgreSQL de produção.

Cenários cobertos:

- Cadastro, login e autenticação.
- Credenciais inválidas e validação de cadastro.
- Criação, consulta, atualização e exclusão de tarefas.
- Tratamento de requisições inválidas e recursos inexistentes.
- Bloqueio de acesso sem autenticação.
- Proteção contra acesso, alteração e exclusão de tarefas de outros usuários.
- Consulta e atualização do perfil.

### Testes unitários

**Tecnologias:** JUnit 5 e Mockito.

Os testes isolam a camada de serviço por meio de mocks dos repositórios e verificam:

- Criação de tarefas associadas ao usuário autenticado.
- Tratamento de usuários ou tarefas inexistentes.
- Listagem de tarefas e retorno de listas vazias.
- Restrições de acesso às tarefas.
- Conversão de entidades para DTOs de resposta.

### Validação da interface

O frontend também foi testado manualmente, incluindo a integração com a API e o funcionamento do **drag and drop**. A movimentação de uma tarefa atualiza seu estado no backend.

---

## 🏗️ Arquitetura e decisões técnicas

O projeto separa a interface da API backend, que é responsável pelas regras de negócio e pelo acesso aos dados.

- **API REST:** endpoints para autenticação, perfil e gerenciamento de tarefas.
- **DTOs:** separação entre os dados recebidos pela API, as entidades persistidas e as respostas.
- **Spring Data JPA e Hibernate:** mapeamento objeto-relacional e persistência.
- **Flyway:** controle de versões e execução de migrações do banco.
- **Spring Security e JWT:** autenticação e proteção dos recursos.
- **Controle de propriedade:** as operações sobre tarefas consideram o usuário autenticado.
- **Testes isolados:** H2 para os testes de integração e Mockito para simular dependências nos testes unitários.

---

## ☁️ Deploy

A aplicação está publicada com serviços separados:

| Componente | Tecnologia |
|---|---|
| Frontend | Render Static Site |
| Backend | Render Web Service |
| Banco de dados | PostgreSQL gerenciado pelo Neon |
| Migrações | Flyway |
| Containerização | Docker |

O backend utiliza variáveis de ambiente para configuração do banco de dados e do segredo JWT, evitando manter credenciais diretamente no código-fonte.

---

## 💻 Executar localmente

### Pré-requisitos

- JDK compatível com a versão do projeto.
- Git.
- PostgreSQL, caso queira executar o backend localmente usando esse banco.
- Visual Studio Code com Live Server, para servir o frontend.

### 1. Clonar o repositório

```bash
git clone https://github.com/PedroLins04/Todo-API.git
cd Todo-API
```

### 2. Configurar as variáveis de ambiente

Configure as variáveis utilizadas pela aplicação:

| Variável | Descrição |
|---|---|
| `DB_URL` | URL JDBC do PostgreSQL |
| `DB_USERNAME` | Usuário do banco |
| `DB_PASSWORD` | Senha do banco |
| `JWT_SECRET` | Segredo para assinatura dos tokens JWT |

Os nomes devem corresponder aos definidos na configuração do projeto. Não compartilhe credenciais reais no repositório.

### 3. Iniciar o backend

No Windows, execute na raiz do projeto:

```powershell
.\mvnw.cmd spring-boot:run
```

Por padrão, a API fica disponível em `http://localhost:8080`.

### 4. Iniciar o frontend

Abra a pasta do frontend no VS Code e execute o arquivo `index.html` com a extensão Live Server.

Para execução local, confira se a URL configurada no JavaScript aponta para:

```javascript
const API_URL = "http://localhost:8080";
```

### 5. Executar os testes

Para executar os testes automatizados:

```powershell
.\mvnw.cmd test
```

---

## 👨‍💻 Desenvolvedor

**Pedro Henrique Azevedo Lins**

Desenvolvedor em formação, com foco em Java, Spring Boot, APIs REST, segurança de aplicações e bancos de dados relacionais.

- **GitHub:** [PedroLins04](https://github.com/PedroLins04)
- **LinkedIn:** [Pedro Lins](www.linkedin.com/in/pedro-lins04)

---

*Projeto desenvolvido para consolidar conhecimentos em desenvolvimento backend e full stack, autenticação, persistência de dados, testes automatizados, arquitetura de APIs REST e deploy.*
