use dbcitech;
describe ci_os;
describe ci_client;
select * from ci_os;

select os,date_format(data_os, '%d/%m/%Y - %H:%i'),tipo,situacao,equipamento,modelo,marca,acessorios,defeito,defeito_constatado,servico_executado,tecnico,valor,id_cli from ci_os where os = 1;


-- ainstrução abaixo seleciona e ordena por nome todos os clientes cadastrados
SELECT * FROM ci_client ORDER BY nome_cli;

-- o bloco de instruções abaixo faz a seleção e união de duas ou mais tabelas
-- OSER variavelque contem os campos desejados da tabela OS
-- CLI variavel que contem os campos desejados da tabela clientes

SELECT 
OSER.os,data_os,tipo,situacao,equipamento,valor, 
CLI.nome_cli, fone_cli
FROM ci_os AS OSER INNER JOIN ci_client AS CLI ON (CLI.id_cli = OSER.id_cli);


SELECT MAX(os) FROM ci_os;
