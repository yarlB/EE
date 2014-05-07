DROP TABLE IF EXISTS zones CASCADE;
CREATE TABLE zones (
       id_zone SERIAL,
       nom VARCHAR(20),
       z BOX, 
       CONSTRAINT pk_zones PRIMARY KEY (id_zone)
);

DROP TABLE IF EXISTS objets CASCADE;
CREATE TABLE objets (
       id_objet SERIAL,
       nom VARCHAR(20),
       precond INTEGER[],
       effet VARCHAR(50),
       CONSTRAINT pk_objets PRIMARY KEY (id_objet)
);

-- FAIRE index
DROP TABLE IF EXISTS zone_objets CASCADE;
CREATE TABLE zone_objets (
       id_zone INTEGER,
       id_objet INTEGER,
       CONSTRAINT fk_zone_objets_id_zone FOREIGN KEY (id_zone) REFERENCES zones(id_zone),
       CONSTRAINT fk_zone_objets_id_objet FOREIGN KEY (id_objet) REFERENCES objets(id_objet)
);


DROP TABLE IF EXISTS predicats CASCADE;
CREATE TABLE predicats (
       id_predicat SERIAL,
       arite INT,
       vraipour INTEGER[],
       CONSTRAINT pk_predicats PRIMARY KEY (id_predicat)
);

DROP TABLE IF EXISTS constantes CASCADE;
CREATE TABLE constantes (
       id_constante SERIAL,
       symbole VARCHAR(20),
       CONSTRAINT pk_constantes PRIMARY KEY (id_constante)
);

-- DEFINIR volatilité minimum

DROP FUNCTION IF EXISTS pioche(p point) CASCADE;
CREATE FUNCTION pioche(p point) RETURNS TABLE(zones integer) AS $$
       SELECT id_zone FROM zones WHERE z @> p;
$$ LANGUAGE SQL;