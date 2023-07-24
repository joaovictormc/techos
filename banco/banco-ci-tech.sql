-- cria banco de dados
create database dbcitech;

-- escolhe o banco de dados
use dbcitech;

-- LOGIN DE USUARIO
create table ciuser(
	id_user int primary key,
    usuario varchar(100) not null,
    fone varchar(20) not null,
    login varchar(20) not null unique,
    senha varchar(20) not null
);

-- descreve a tabela
describe ciuser;

-- INSERE DADOS NA TABELA create -> insert (CRUD)
insert into ciuser(id_user,usuario,fone,login,senha)
values(1,'João','28 99999-9999','joaovictormc','12345');

-- EXIBE DADOS NA TABELA read -> select (CRUD)
select * from ciuser;

insert into ciuser(id_user,usuario,fone,login,senha)
values(2,'Administrador','28 99999-9999','admin','admin');
insert into ciuser(id_user,usuario,fone,login,senha)
values(3,'Zé Vitor','28 99999-9999','zvitor','12345');

-- MODIFICA DADOS NA TABELA update
update ciuser set fone='28 88888-8888' where id_user='2';

-- APAGA UM DADO NA TABELA delete
delete from ciuser where id_user=3;
select * from ciuser;




-- CADASTRO DE CLIENTES

create table ci_client(
	id_cli int primary key auto_increment,
    nome_cli varchar(150) not null,
    
    rua_cli varchar(255),
    bairro_cli varchar(255),
    cep_cli varchar(9),
    cidade_cli varchar(255),
    estado_cli varchar(2),
    
    fone_cli varchar(20) not null,
    email_cli varchar(100),
    cpf_cli varchar(14),
    cnpj_cli varchar(18)
);

describe ci_client;
select id_cli as ID, nome_cli as Nome, rua_cli as Rua, bairro_cli as Bairro, cep_cli as CEP, cidade_cli as Cidade, estado_cli as UF, fone_cli as Telefone, email_cli as EMail, cpf_cli as CPF, cnpj_cli as CNPJ from ci_client;


-- drop table ci_client;

insert into ci_client(nome_cli,rua_cli,bairro_cli,cep_cli,cidade_cli,estado_cli,fone_cli,email_cli,cpf_cli,cnpj_cli)
values('João V','rua XXXX','bairro XXXXX','12345-678','XXXXX','ES','28 99999-9999','joaov@gmail.com','123.456.789-01','12.345.678/0001-90');

select * from ci_client;




-- CADASTRO DE ORDEM DE SERVIÇO

create table ci_os(
	os int primary key auto_increment,
    
    -- ao realizar um insert, ira puxar a data e hora do servidor e insere no campo da tabela
    data_os timestamp default current_timestamp, 
    
    equipamento varchar(50),
    modelo varchar(50),
    marca varchar(50),
    acessorios varchar(100),
    
    defeito varchar(255) not null,
    defeito_constatado varchar(255),
    servico_executado varchar(255),
    tecnico varchar(150),
    valor decimal(10,2),
    
    -- relacionamento da OS com a tebela cliente
    id_cli int not null,
    foreign key(id_cli) references ci_client(id_cli)
);
describe ci_os;

-- a linha abaixo altera a tabela adicionando um campo em uma determinada posição 
ALTER TABLE ci_os ADD tipo varchar(50) NOT NULL AFTER data_os;
ALTER TABLE ci_os ADD situacao varchar(60) NOT NULL AFTER tipo;

insert into ci_os(equipamento,modelo,marca,acessorios,defeito,defeito_constatado,servico_executado,tecnico,valor,id_cli)
values('MacBook Pro','A1278','Apple','com fonte','não liga','placa com falha','reparo da placa','joão',1300.89,1);

select * from ci_os;




-- CODIGO ABAIXO TRAZ INFORMAÇÕES DE DUAS TABELAS
select
	O.os,equipamento,modelo,marca,acessorios,defeito,defeito_constatado,servico_executado,valor,
    C.nome_cli,rua_cli,bairro_cli,cep_cli,cidade_cli,estado_cli,fone_cli,cpf_cli,cnpj_cli
from ci_os as O inner join ci_client as C on (O.id_cli = C.id_cli);