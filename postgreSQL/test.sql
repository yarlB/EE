WITH placements_aux AS (
     (SELECT id_zone FROM zones WHERE z @> p) as zones_as
     NATURAL JOIN (SELECT id_zones, id_program FROM placements WHERE trig = false) as placements_as
     ), triggering AS (
     UPDATE placements SET trig = true WHERE id_placement IN placements_aux
     RETURNING *
     )
SELECT * FROM triggering;