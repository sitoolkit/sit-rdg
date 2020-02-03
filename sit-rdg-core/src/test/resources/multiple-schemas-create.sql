-- スキーマ１のテーブル
create table SCHEMA1.TABLE1 (
    COLUMN1 VARCHAR2(2)
  , COLUMN2 NUMBER(2, 0)
  , constraint TABL1_PKC primary key (COLUMN1)
) ;
-- スキーマ２のテーブル
create table SCHEMA2.TABLE1 (
  COLUMNA VARCHAR2(2)
  , COLUMNB NUMBER(2, 0)
  , constraint TABL1_PKC primary key (COLUMNA)
) ;