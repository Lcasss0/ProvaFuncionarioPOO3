Este projeto é um sistema de gerenciamento de funcionários desenvolvido em Java utilizando JavaFX para a interface gráfica.
Ele permite cadastrar, consultar, listar e excluir funcionários de uma empresa, incluindo seus dados pessoais e endereço, com persistência em arquivo CSV e geração de relatórios simples utilizando Stream API do Java.

O sistema foi desenvolvido de forma simples e funcional, sem o uso de FXML, para facilitar a execução diretamente no IntelliJ IDEA, evitando problemas com caminhos de arquivo.

Funcionalidades

O sistema oferece as seguintes funcionalidades:

Cadastro de Funcionários

Cadastro completo com dados pessoais: matrícula, nome, CPF, data de nascimento, cargo, salário, data de contratação.

Endereço completo: logradouro, número, complemento, bairro, cidade, estado e CEP.

Validação básica de dados (ex.: valores numéricos, formatos de datas e campos obrigatórios).

Exclusão de Funcionários

Permite selecionar um funcionário na tabela e removê-lo do sistema.

Listagem de Funcionários

Exibe todos os funcionários cadastrados em uma tabela, incluindo matrícula, nome, cargo e cidade.

Relatórios com Stream API

Relatório por Cargo: filtra funcionários por cargo específico.

Média Salarial por Cargo: calcula e exibe a média salarial de cada cargo registrado.

Persistência em CSV

Todos os dados são salvos em arquivo funcionarios.csv.

Os dados são carregados automaticamente ao iniciar o sistema.

As alterações são salvas após cada operação (adicionar/excluir).

Estrutura do Projeto
funcionarioapp/
 ├── Main.java                 # Classe principal, cria toda a interface e controla eventos
 ├── model/
 │    ├── Endereco.java        # Classe modelo para Endereço
 │    └── Funcionario.java     # Classe modelo para Funcionário
 └── util/
      └── CSVUtil.java         # Classe utilitária para salvar e carregar dados em CSV

Pacotes

model: contém as classes que representam os dados do sistema (Funcionario e Endereco).

util: contém a classe CSVUtil responsável pela persistência em CSV.

Main.java: implementa a interface gráfica, eventos e controle do sistema sem uso de FXML.

Tecnologias Utilizadas

Java 17+

JavaFX 21 (ou superior)

Collections e Streams do Java para manipulação e relatórios

Arquivo CSV para persistência de dados
