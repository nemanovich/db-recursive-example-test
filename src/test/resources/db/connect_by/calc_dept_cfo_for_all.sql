CREATE OR REPLACE PROCEDURE calc_dept_cfo
AS
  BEGIN
    FOR rec IN (SELECT depart.depart_id                                                                         AS id,
                       regexp_replace(SYS_CONNECT_BY_PATH(depart_add.add_value, '|'), '.*\|([^\|]+)\|*$', '\1') AS cfo
                FROM hierarchy h
                       INNER JOIN depart ON depart.depart_id = h.child_id
                       LEFT JOIN depart_add ON depart.depart_id = depart_add.depart_id AND depart_add.add_type = 'CFO'
                START WITH nvl(h.parent_id, 0) = 0
                CONNECT BY h.parent_id = PRIOR h.child_id
        FOR UPDATE
    )
    LOOP
      update_dept_cfo(rec.id, rec.cfo);
    END LOOP;
  END;