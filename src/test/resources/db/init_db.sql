CREATE TABLE depart (
  depart_id   NUMBER(20) PRIMARY KEY NOT NULL,
  depart_name VARCHAR(100)
);

CREATE TABLE depart_add (
  depart_id NUMBER(20) PRIMARY KEY NOT NULL,
  add_type  VARCHAR2(100),
  add_value VARCHAR2(100)
);

CREATE TABLE hierarchy (
  parent_id NUMBER(20),
  child_id  NUMBER(20)
);

CREATE TABLE dept_cfo (
  depart_id NUMBER(20) PRIMARY KEY NOT NULL,
  cfo       VARCHAR2(100)
);

/*
Отдел1 - доп.инфо. 'CFO'='D1'
  Служба11
    Группа111
    Группа112
  Служба12 - доп.инфо. 'CFO'='S12'
    Группа121
    Группа122 - доп.инфо. 'CFO'=null
    Группа123 - доп.инфо. 'CFO'='G123'
 */

INSERT INTO depart
VALUES (1, 'Отдел1');
INSERT INTO depart
VALUES (2, 'Служба11');
INSERT INTO depart
VALUES (3, 'Группа111');
INSERT INTO depart
VALUES (4, 'Группа112');
INSERT INTO depart
VALUES (5, 'Служба12');
INSERT INTO depart
VALUES (6, 'Группа121');
INSERT INTO depart
VALUES (7, 'Группа122');
INSERT INTO depart
VALUES (8, 'Группа123');

INSERT INTO depart_add
VALUES (1, 'CFO', 'D1');
INSERT INTO depart_add
VALUES (5, 'CFO', 'S12');
INSERT INTO depart_add
VALUES (7, 'CFO', NULL);
INSERT INTO depart_add
VALUES (8, 'CFO', 'G123');

INSERT INTO hierarchy
VALUES (NULL, 1);
INSERT INTO hierarchy
VALUES (1, 2);
INSERT INTO hierarchy
VALUES (1, 5);

INSERT INTO hierarchy
VALUES (2, 3);
INSERT INTO hierarchy
VALUES (2, 4);
INSERT INTO hierarchy
VALUES (5, 6);
INSERT INTO hierarchy
VALUES (5, 7);
INSERT INTO hierarchy
VALUES (5, 8);