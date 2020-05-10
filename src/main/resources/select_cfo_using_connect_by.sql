SELECT lpad(' ', 3 * (level - 1)) || depart.depart_name                                    AS name,
       depart.depart_id                                                                    AS id,
       d_add.add_value                                                                     AS cfo_raw,
       regexp_replace(SYS_CONNECT_BY_PATH(d_add.add_value, '|'), '.*\|([^\|]+)\|*$', '\1') AS cfo
FROM hierarchy h
       INNER JOIN depart ON depart.depart_id = h.child_id
       LEFT JOIN depart_add d_add ON depart.depart_id = d_add.depart_id AND d_add.add_type = 'CFO'
START WITH nvl(h.parent_id, 0) = 0
CONNECT BY h.parent_id = PRIOR h.child_id;
/*
Учесть:
- разделитель
- parent_id
- длину SYS_CONNECT_BY_PATH = 4000
 */