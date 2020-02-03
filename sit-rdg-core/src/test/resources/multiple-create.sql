-- テーブルの和名のコメント1
create table CamelCaseTable (
  VarcharColumn1 VARCHAR2(2) not null
  , VarcharColumn2 VARCHAR2(1) not null
  , DateColumn DATE not null
  , VarcharColumnMeansDate VARCHAR2(8) not null
  , DecimalColumn NUMBER(12, 5)
  , NumberColumn NUMBER(10, 0) not null
  , constraint CamelCaseTable_PKC primary key (VarcharColumn1,VarcharColumn2)
) ;

-- テーブルの和名のコメント2
create table SNAKE_CASE_TABLE (
  VARCHAR_COLUMN1 VARCHAR2(2) not null
  , VARCHAR_COLUMN2 VARCHAR2(1) not null
  , DATE_COLUMN DATE not null
  , VARCHAR_COLUMN_MEANS_DATE VARCHAR2(8) not null
  , DECIMAL_COLUMN NUMBER(12, 5)
  , NUMBER_COLUMN NUMBER(10, 0) not null
  , constraint CAMEL_CASE_TABLE_PKC primary key (VARCHAR_COLUMN1,VARCHAR_COLUMN2)
) ;

-- ダブルクォートを利用したテーブル
create table "DoubleQuotedTable" (
   "NumberColumn" NUMBER(10, 0) not null
  , "VarcharColumn1" VARCHAR2(10) not null
  , "VarcharColumn2" VARCHAR2(2) not null
  , "DateColumn" DATE not null
  , "VarcharColumnMeansDate" VARCHAR2(8) not null
  , "DecimalColumn" NUMBER(12, 5)
  , constraint "DoubleQuotedTable_PKC" primary key ( "NumberColumn")
) ;