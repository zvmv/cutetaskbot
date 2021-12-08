drop table botuser;
create table botuser (
    id integer,
    name varchar,
    username varchar,
    firstname varchar,
    lastname varchar,
    performer bool,
    admin bool,
    state varchar,
    chatid integer,
    primary key(id)
);

drop table task;
create table task (
    id serial,
    description varchar not null,
    createdate date not null,
    maxdate date,
    finishdate date,
    finished bool not null,
    createdby integer not null,
    primary key(id)
);


