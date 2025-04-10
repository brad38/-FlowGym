# 🏋️ FlowGym

🗃️ Requisitos de Banco de Dados
Esta é uma aplicação que necessita de acesso a um banco de dados local.
O projeto não está configurado com Flyway para criação automática de schema, portanto:

➡️ É necessário que o usuário crie manualmente o banco de dados de acordo com a estrutura esperada no código (entidades/tabelas).

Você pode verificar os arquivos .java da camada de modelo (model) para seguir a estrutura correta.

**FlowGym** é um sistema de gerenciamento para academias, desenvolvido em **Java** com **Spring Boot**. O objetivo do projeto é facilitar o controle de alunos, planos, treinos e outras funcionalidades essenciais para a administração de uma academia.

## 🚀 Funcionalidades Principais

- Cadastro de alunos e professores  
- Gestão de planos de treino  
- Registro de frequência  
- Controle de mensalidades  
- Relatórios básicos  
- Interface web responsiva (via Spring Boot)

## 🛠️ Tecnologias Utilizadas

- Java 17+  
- Spring Boot  
- Maven  
- JPA / Hibernate  
- **PostgreSQL**  
- Git
  
## 📦 Como executar o projeto

1. **Clone o repositório**:
   ```bash
   git clone https://github.com/brad38/-FlowGym.git
   cd FlowGym

Configure o banco de dados PostgreSQL:

Crie um banco de dados no PostgreSQL com o nome e credenciais conforme definidos no application.properties.

Certifique-se de que o PostgreSQL está rodando na máquina local.   

Execute via terminal ou IDE:
Pelo terminal:
./mvnw spring-boot:run
Ou abra com sua IDE favorita (IntelliJ, Eclipse, VS Code) e execute a classe Application.java

Acesse a aplicação:
URL padrão: http://localhost:8080


💡 Possíveis melhorias futuras
Integração com métodos de pagamento
Dashboard com estatísticas gráficas
Controle de acesso com autenticação e autorização (Spring Security)
Aplicativo mobile com API REST

## 📄 Licença

Este projeto é de uso educacional e está licenciado sob os termos da [Licença MIT](./LICENSE).

👥 Desenvolvido por
Este projeto foi desenvolvido em grupo por:
Cauan Frias
João Ponte
Gabriel Aian
Bradsson Santos
