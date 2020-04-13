create table tab_1 (
  col_1_1 char(2),
  col_1_2 char(4),
  constraint tab_1_pk primary key (col_1_1, col_1_2),
  foreign key (col_1_1, col_1_2) references tab_1(col_1_1, col_1_2)
);

create table tab_2 (
  col_2_1 char(2),
  col_2_2 char(4),
  col_2_3 char(6),
  foreign key (col_2_1, col_2_2) references tab_1(col_1_1, col_1_2)
);


create table tab_3 (
  col_3_1 char(2),
  col_3_2 char(4),
  col_3_3 char(4),
  foreign key (col_3_1, col_3_2) references tab_1(col_1_1, col_1_2),
  foreign key (col_3_1, col_3_2, col_3_3) references tab_2(col_2_1, col_2_2, col_2_3)
);
