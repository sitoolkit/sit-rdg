create table tab_1 (
  col_1_1 int primary key
);

create table tab_2 (
  col_2_1 int,
  foreign key (col_2_1) references tab_1(col_1_1)
);


create table tab_3 (
  col_3_1 int
);


alter table tab_3 add constraint tab3_tab1 foreign key (col_3_1) references tab_1(col_1_1);

