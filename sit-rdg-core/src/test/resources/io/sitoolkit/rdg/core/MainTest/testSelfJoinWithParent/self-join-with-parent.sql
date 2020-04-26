create table tab_1 (
  col_1_1 char(2),
  constraint tab_1_pk primary key (col_1_1)
);

create table tab_2 (
  col_2_1 char(2),
  col_2_2 char(4),
  col_2_3 char(2),
  col_2_4 char(4),
  foreign key (col_2_1) references tab_1(col_1_1),
  foreign key (col_2_3, col_2_4) references tab_2(col_2_1, col_2_2)
);
