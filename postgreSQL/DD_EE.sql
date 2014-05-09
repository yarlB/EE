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

--CE QUI SUIT N'EST PAS UTILISABLE RIGHT NOW 

DROP TABLE IF EXISTS substitutions CASCADE;
CREATE TABLE substitutions (
       id_substitution SERIAL,
       theta INTEGER[],
       CONSTRAINT pk_substitutions PRIMARY KEY(id_substitution)
);

DROP TABLE IF EXISTS predicats CASCADE;
CREATE TABLE predicats (
       id_predicat SERIAL,
       id_substitution INTEGER, --substitution(s) pour la(les) quelle(s) le prédicat est vrai
       CONSTRAINT pk_predicats PRIMARY KEY (id_predicat),
       CONSTRAINT fk_predicats_id_substitution FOREIGN KEY (id_substitution) REFERENCES substitutions(id_substitution)
);

DROP TABLE IF EXISTS constantes CASCADE;
CREATE TABLE constantes (
       id_constante SERIAL,
       symbole VARCHAR(20),
       CONSTRAINT pk_constantes PRIMARY KEY (id_constante)
);

DROP TABLE IF EXISTS preconditions CASCADE;
CREATE TABLE preconditions (
       id_precondition SERIAL,
       id_predicat INTEGER,
       id_substitution INTEGER,
       CONSTRAINT pk_preconditions PRIMARY KEY(id_precondition),
       CONSTRAINT fk_preconditions_id_predicat FOREIGN KEY (id_predicat) REFERENCES predicat(id_predicat),
       CONSTRAINT fk_preconditions_id_substitution FOREIGN KEY (id_substitution) REFERENCES substitutions(id_substitution)
);

DROP TABLE IF EXISTS objet_preconditionss CASCADE;
CREATE TABLE objet_preconds (
       id_objet INTEGER,
       id_precondition INTEGER,
       CONSTRAINT fk_object_preconditions_id_objet FOREIGN KEY (id_objet) REFERENCES objets(id_objet),
       CONSTRAINT fk_object_preconditions_id_precondition FOREIGN KEY (id_precondition) 
       REFERENCES preconditions(id_precondition)
);




-- DEFINIR volatilités minimums

DROP FUNCTION IF EXISTS pioche(p point) CASCADE;
CREATE FUNCTION pioche(p point) RETURNS TABLE(zones integer) AS $$
       SELECT id_zone FROM zones WHERE z @> p;
$$ LANGUAGE SQL;

