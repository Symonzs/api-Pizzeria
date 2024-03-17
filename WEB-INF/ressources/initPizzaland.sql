DROP TABLE IF EXISTS pizzas CASCADE;
DROP TABLE IF EXISTS ingredients CASCADE;
DROP TABLE IF EXISTS commandes CASCADE;
DROP TABLE IF EXISTS contient;
DROP TABLE IF EXISTS liste;

CREATE TABLE ingredients (
    ino INTEGER,
    iname VARCHAR(255) UNIQUE,
    iprice DECIMAL(5,2),
    CONSTRAINT pk_ingredients PRIMARY KEY (ino)
);

CREATE TABLE pizzas (
    pino INTEGER,
    piname VARCHAR(255) UNIQUE,
    pipate VARCHAR(255),
    pibase VARCHAR(255),
    CONSTRAINT pk_pizzas PRIMARY KEY (pino)
);

CREATE TABLE commandes (
    cno INTEGER,
    cname VARCHAR(255),
    cdate BIGINT,
    CONSTRAINT pk_commandes PRIMARY KEY (cno)
);

CREATE TABLE contient (
    pino INTEGER,
    ino INTEGER,
    CONSTRAINT pk_contient PRIMARY KEY (pino, ino),
    CONSTRAINT fk_contient_pizzas FOREIGN KEY (pino) REFERENCES pizzas(pino)
        ON DELETE CASCADE,
    CONSTRAINT fk_contient_ingredients FOREIGN KEY (ino) REFERENCES ingredients(ino)
        ON DELETE CASCADE
);

CREATE TABLE liste (
    cno INTEGER,
    pino INTEGER,
    pqte INTEGER,
    CONSTRAINT pk_liste PRIMARY KEY (cno, pino),
    CONSTRAINT fk_liste_commandes FOREIGN KEY (cno) REFERENCES commandes(cno)
        ON DELETE CASCADE,
    CONSTRAINT fk_liste_pizzas FOREIGN KEY (pino) REFERENCES pizzas(pino)
        ON DELETE CASCADE
);