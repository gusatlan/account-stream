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

Para executar é necessário ter o docker na máquina

```
./run
```