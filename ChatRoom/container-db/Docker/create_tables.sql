CREATE TABLE IF NOT EXISTS Utente(
username VARCHAR(10) PRIMARY KEY,
password VARCHAR(10)
);

CREATE TABLE IF NOT EXISTS Stanza(
nome VARCHAR(30),
numero SERIAL PRIMARY KEY,
capacita integer,
descrizione varchar(100),
proprietario VARCHAR(50) REFERENCES Utente(username)
);

CREATE TABLE IF NOT EXISTS Messaggio(
corpo VARCHAR(100),
tempo_invio timestamp,
mittente VARCHAR(50) references Utente(username),
stanza INTEGER references Stanza(numero)
);



CREATE TABLE IF NOT EXISTS utentistanza(
    username VARCHAR(10) REFERENCES Utente(username),
    numerostanza integer REFERENCES Stanza(numero),
    approvato BOOLEAN,
    UNIQUE(username,numerostanza)
);

CREATE SEQUENCE sequenza_stanze
start 100
increment 2;

ALTER SEQUENCE IF EXISTS stanza_numero_seq
INCREMENT BY 2
START WITH 100
MINVALUE 100
RESTART