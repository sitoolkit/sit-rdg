-- テーブル定義１
create table TABLE1 (
    GROUP1_COLUMN NUMBER(2, 0)
  , GROUP2_COLUMN NUMBER(3, 0)
  , NOTE VARCHAR2(255)
  , constraint TABL1_PKC primary key (GROUP1_COLUMN, GROUP2_COLUMN)
) ;
-- テーブル定義２
create table TABLE2 (
    GROUP1_COLUMN NUMBER(2, 0)
  , COLUMN2 VARCHAR2(1)
  , constraint TABLE2_PKC primary key (GROUP1_COLUMN)
) ;
-- テーブル定義３
create table TABLE3 (
    GROUP2_COLUMN NUMBER(3, 0)
  , COLUMN2 VARCHAR2(1)
  , constraint TABLE3_PKC primary key (GROUP2_COLUMN)
) ;

select t1.note from table1 t1, table2 t2, table3 t3 where t1.GROUP1_COLUMN = t2.GROUP1_COLUMN and t1.GROUP2_COLUMN = t3.GROUP2_COLUMN