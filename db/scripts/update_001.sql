create table person (
    id serial primary key not null,
    username varchar(2000) unique,
    password varchar(2000)
);

insert into person (username, password) values ('parsentev', '123');
insert into person (username, password) values ('ban', '123');
insert into person (username, password) values ('ivan', '123');