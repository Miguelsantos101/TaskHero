# TaskHero

Especificações do Projeto: **TaskHero - Gerenciador de Tarefas Gamificado**

Este documento descreve as especificações do projeto final da disciplina de Programação de Dispositivos Móveis.

## 1. Alunos do Grupo
- Miguel dos Santos Flores

## 2. Visão Geral do Software
O TaskHero é um aplicativo para o sistema Android projetado para ajudar os usuários a gerenciar suas tarefas diárias de uma forma mais motivadora e interativa. A principal diferença de um gerenciador de tarefas comum é a introdução de elementos de "gamificação": os usuários ganham pontos (escore) por cada tarefa concluída, transformando a produtividade em um desafio pessoal. O aplicativo exigirá cadastro e login, e cada usuário terá seu próprio perfil com foto e pontuação.

## 3. Papéis
- **Usuário**: O único papel no sistema. Após realizar o cadastro e login, o usuário pode criar, visualizar, editar, marcar como concluídas e excluir suas próprias tarefas. Ele também pode visualizar seu perfil, incluindo sua foto e o escore total acumulado.

## 4. Requisitos Funcionais
- **Usuários que o app permite**:
  - Apenas usuários cadastrados poderão acessar a área principal do aplicativo para gerenciar tarefas. 

- **O que cada usuário pode fazer**:
  - Realizar cadastro no sistema fornecendo nome, e-mail, senha e uma foto de perfil. 
  - Fazer login com e-mail e senha.
  - Criar novas tarefas (com título, descrição e data/hora de prazo).
  - Visualizar a lista de tarefas pendentes e concluídas.
  - Editar os detalhes de uma tarefa existente.
  - Marcar uma tarefa como "concluída", o que irá gerar pontos.
  - Excluir uma tarefa.
  - Visualizar e editar seu perfil (foto e nome).
  
- **Entradas necessárias**:
  - **Cadastro**: Nome, e-mail, senha e foto do usuário. 
  - **Login**: E-mail e senha. 
  - **Criação de Tarefa**: Título (texto), descrição (texto), data e hora do prazo.

- **Processamento que o app realiza**:
  - O aplicativo irá guardar o **hash da senha** do usuário para garantir a segurança. 
  - O app irá **armazenar a foto do usuário** associada ao seu perfil. 
  - O sistema irá **associar as atividades (tarefas) aos usuários** que as criaram. 
  - Ao marcar uma tarefa como concluída, o app irá **calcular e guardar o escore do usuário**, atualizando sua pontuação total. 
  - O app utilizará **alarmes e notificações** para alertar o usuário sobre os prazos das tarefas.
  
- **Relatórios e Saídas do aplicativo**:
  - Tela principal com a listagem de tarefas do usuário, separadas por pendentes e concluídas.
  - Tela de perfil exibindo a foto, nome e escore total do usuário.
  - Notificações no dispositivo para lembrar o usuário de uma tarefa que está próxima do vencimento.
  - Mensagens de erro para entradas inválidas (ex: campos de login/cadastro vazios). 
