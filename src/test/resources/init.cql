CREATE KEYSPACE test
  WITH REPLICATION = {
   'class' : 'SimpleStrategy',
   'replication_factor' : 1
  };

USE test;

CREATE TABLE IF NOT EXISTS user (
    email text PRIMARY KEY,
    username text,
    firstname text,
    surname text,
    age int
);