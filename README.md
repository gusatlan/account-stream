# account-stream - Stream de contas
2023-07-27

## Conceito

Desenvolvido para lidar com o *event-stream* das transações de contas.

### Requisitos

1. Escutar o tópico do kafka connect que lê o banco de dados relacional.
    1. Converter as mensagens do tópico para o payload esperado;
    2. Após a conversão, enviar a mensagem para o tópico que gerenciará a atualização no MongoDB;
2. Escutar o tópico de atualização de dados para o MongoDB;
    1. Persistir ou excluir os dados no MongoDB;

Note que esse projeto se encarrega de 4 tópicos kafka

1. Tópicos de transformação dos eventos das tabelas **account** e **account_transaction**;
2. Tópicos de atualização no Mongo de **BankAccount** e **BankAccountEvent**;


Para executar é necessário ter o docker na máquina

```
./run
```

Para executar toda a arquitetura:

```
./runAll
```

## Kafka Connect

É usado para migrar os dados do serviço antigo **account-old** para tópico kafka do **account-stream** e esse realiza a atualização no banco de dados Mongo.

Para configurar o *Kafka Connect* vá em http://localhost:9021 (*Control Center*)

1. Clicar em *CO Cluster 1*;
2. Clicar no menu *Connect*;
3. Clicar em *connect-default*;
4. Clicar em *Add connector*;
5. Clicar em *Upload connector config file*;
6. Selecionar o arquivo **connector_postgres-connector_config.properties**;
7. Clicar em *Continue*;
8. Clicar em *Launch*;


