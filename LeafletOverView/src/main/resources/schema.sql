CREATE TABLE IF NOT EXISTS tiles
(
    z integer,
    x integer,
    y integer,
    tilebytearray bytea NOT NULL,
    source character varying(50),
    addingtime character varying(50),
    PRIMARY KEY (x,y,z)
);
CREATE TABLE IF NOT EXISTS tifftiles
(
    z integer,
    x integer,
    y integer,
    tilebytearray bytea NOT NULL,
    source character varying(50),
    addingtime character varying(50),
    PRIMARY KEY (x,y,z)
);