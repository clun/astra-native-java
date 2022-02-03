 CREATE TABLE IF NOT EXISTS todoitems (
   user_id         TEXT,
   item_id         TIMEUUID,
   title           TEXT,
   completed       BOOLEAN,
   offset          INT,
   PRIMARY KEY ((user_id), item_id)
) WITH CLUSTERING ORDER BY (item_id ASC);

insert into todoitems (user_id, item_id, completed,title,offset) VALUES('john',0424ce30-7de0-11ec-8b0c-ef19ebbbe826,false,'Sample',0);
