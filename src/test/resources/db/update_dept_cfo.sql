CREATE OR REPLACE PROCEDURE update_dept_cfo(id_value IN NUMBER, cfo_value IN VARCHAR2) AS
  BEGIN
    INSERT INTO dept_cfo (depart_id, cfo) VALUES (id_value, cfo_value);
    EXCEPTION
    WHEN dup_val_on_index
    THEN
      UPDATE dept_cfo SET cfo = cfo_value WHERE depart_id = id_value;
  END;
