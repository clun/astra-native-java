# Run Java Native Microservices against Astra

*This is work in progress with some notes*

How to use Astra with Micronaut, Spring Boot, Quarkus, Helidron with Astra, normal and native compilation

## Prerequisites

- Install a JDK and Graal VM (native images)
- Install Docker and Docker-compose (containers)
- Install Maven
- Install an IDE

## Astra

- Create Account
- Create Database
- Create Token
- Create the table
```sql
CREATE TABLE todoitems (
    user_id text,
    item_id timeuuid,
    completed boolean,
    title text,
    offset int,
    PRIMARY KEY ((user_id), item_id)
) WITH CLUSTERING ORDER BY (item_id ASC);
```

- Some operations
```
INSERT INTO todoitems (user_id, item_id, completed, title) VALUES ( 'john', 11111111-5cff-11ec-be16-1fedb0dfd057, true, 'Walk the dog');
INSERT INTO todoitems (user_id, item_id, completed, title) VALUES ( 'john', 22222222-5cff-11ec-be16-1fedb0dfd057, false, 'Have lunch tomorrow');
INSERT INTO todoitems (user_id, item_id, completed, title) VALUES ( 'mary', 33333333-5cff-11ec-be16-1fedb0dfd057, true, 'Attend the workshop');

// query all items for a given user
SELECT * FROM todoitems WHERE user_id = 'john';

// mark an item as "done" (full primary key must be specified)
UPDATE todoitems SET completed = true WHERE user_id = 'john' AND item_id = 22222222-5cff-11ec-be16-1fedb0dfd057;
SELECT toTimestamp(item_id), completed, title FROM todoitems WHERE user_id = 'john';

// remove an item (full primary key must be specified)
DELETE FROM todoitems WHERE user_id='john' AND item_id=11111111-5cff-11ec-be16-1fedb0dfd057;
SELECT toTimestamp(item_id), completed, title FROM todoitems WHERE user_id = 'john';

// WARNING: this removes *all* rows (we make the table clean and ready for actual usage)
TRUNCATE TABLE todoitems;
```

## Spring

- Access the app builder
```
https://start.spring.io
```


## Micronaut

- Install SDKMAN
```
 curl -s "https://get.sdkman.io" | bash
```

- Init SDK Man
```
source "/Users/cedricklunven/.sdkman/bin/sdkman-init.sh"
```

- Install Micronaut CLI
```
sdk install micronaut
```

- Create sample Apps
```
mn create-app com.datastax.tutorial --build=maven --lang=java
```


## Quarkus

