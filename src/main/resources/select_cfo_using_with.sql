WITH w (name,
       id,
       cfo,
       cfo_raw,
       lvl,
       parent_id) AS (SELECT depart.depart_name AS name,
                             depart.depart_id   AS id,
                             d_add.add_value    AS cfo,
                             d_add.add_value    AS cfo_raw,
                             1,
                             h.parent_id
                      FROM hierarchy h
                             INNER JOIN depart ON depart.depart_id = h.child_id
                             LEFT JOIN depart_add d_add ON depart.depart_id = d_add.depart_id AND d_add.add_type = 'CFO'
                      WHERE nvl(h.parent_id, 0) = 0
    UNION ALL
    SELECT depart.depart_name          AS name,
           depart.depart_id            AS id,
           nvl(d_add.add_value, w.cfo) AS cfo,
           d_add.add_value             AS cfo_raw,
           lvl + 1,
           h.parent_id
    FROM w,
         hierarchy h
           INNER JOIN depart ON depart.depart_id = h.child_id
           LEFT JOIN depart_add d_add ON depart.depart_id = d_add.depart_id AND d_add.add_type = 'CFO'
    WHERE h.parent_id = w.id)
    SEARCH DEPTH FIRST BY name ASC SET sibling_order
SELECT lpad(' ', 3 * (lvl - 1)) || name as name, id, cfo_raw, cfo
FROM w
ORDER BY sibling_order ASC;