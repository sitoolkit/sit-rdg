create table tab_1 (
  col_1_1 int primary key
);


create table tab_2 (
  col_2_1 int,
  primary key (col_2_1)
);


create table tab_3 (
  col_3_1 int
);

create unique index tab_3_pk on tab_3 (col_3_1);
alter table tab_3 add constraint tab_3_pk primary key (col_3_1);


create table tab_4 (
  col_4_1 int
);

alter table tab_4 add unique (col_4_1);


create table tab_5 (
  col_5_1 int primary key,
  col_5_2 int,
  unique(col_5_2)
);

