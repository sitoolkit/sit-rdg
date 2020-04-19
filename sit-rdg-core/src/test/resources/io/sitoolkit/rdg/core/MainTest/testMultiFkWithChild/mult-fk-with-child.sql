create table tab_1 (
  col_1_1 char(3) primary key
);

create table tab_2 (
  col_2_1 char(3),
  col_2_2 char(3),
  foreign key (col_2_1) references tab_1(col_1_1),
  foreign key (col_2_2) references tab_1(col_1_1)
);

create table tab_3 (
  col_3_1 char(3),
  col_3_2 char(3),
  foreign key (col_3_1) references tab_2(col_2_2)
);
