DROP TABLE IF EXISTS zones CASCADE;
CREATE TABLE zones (
       id_zone SERIAL,
       nom VARCHAR(32),
       z BOX, 
       CONSTRAINT pk_zones PRIMARY KEY (id_zone)
);

DROP TABLE IF EXISTS programs CASCADE;
CREATE TABLE programs (
       id_program SERIAL,
       nom VARCHAR(32),
       program TEXT,
       CONSTRAINT pk_programs PRIMARY KEY (id_program)
);

-- FAIRE index
DROP TABLE IF EXISTS placements CASCADE;
CREATE TABLE placements (
       id_placement INTEGER,
       id_zone INTEGER,
       id_program INTEGER,
       trig BOOLEAN,
       CONSTRAINT pk_placements PRIMARY KEY(id_placement),
       CONSTRAINT fk_placements_id_zone FOREIGN KEY (id_zone) REFERENCES zones(id_zone),
       CONSTRAINT fk_placements_id_program FOREIGN KEY (id_program) REFERENCES programs(id_program)
);

/*

-- DEFINIR volatilités minimums
DROP FUNCTION IF EXISTS pioche(p point) CASCADE;
CREATE FUNCTION pioche(p point) RETURNS TABLE (nom programs.nom%TYPE, prog programs.program) AS $$
DECLARE
    
BEGIN
       SELECT nom, program
       FROM 
       (SELECT id_zone FROM zones WHERE z @> p) as zones
       NATURAL JOIN (SELECT id_zone, id_objet FROM zone_objets WHERE trig = false) as z_o
       NATURAL JOIN (SELECT nom, program FROM objets) as objets;
END;
$$ LANGUAGE plpgsql;

DROP FUNCTION IF EXISTS pioche(p point) CASCADE;
CREATE FUNCTION pioche(p point) RETURNS TABLE (nom programs.nom%TYPE, prog programs.program) AS $$
       WITH placements_aux AS (
       	    (SELECT id_zone FROM zones WHERE z @> p) as zones_as
	    	   NATURAL JOIN (SELECT id_zones, id_program FROM placements WHERE trig = false) as placements_as
	    ), triggering AS (
	           UPDATE placements SET trig = true WHERE id_placement IN placements_aux
		   RETURNING *
      	    )
       	    SELECT * FROM triggering;
       	    	   
END;
$$ LANGUAGE plpgsql;

UPDATE placements SET

*/
--CE QUI SUIT N'EST PAS UTILISABLE RIGHT NOW 
/*
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

*/

