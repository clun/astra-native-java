package com.datastaxdev.todo.cassandra;

import static com.datastaxdev.todo.cassandra.TodoServiceCassandraCql.TABLE_TODOITEMS;
import static com.datastaxdev.todo.cassandra.TodoServiceCassandraCql.TODO_COMPLETED;
import static com.datastaxdev.todo.cassandra.TodoServiceCassandraCql.TODO_ITEM_ID;
import static com.datastaxdev.todo.cassandra.TodoServiceCassandraCql.TODO_OFFSET;
import static com.datastaxdev.todo.cassandra.TodoServiceCassandraCql.TODO_TITLE;
import static com.datastaxdev.todo.cassandra.TodoServiceCassandraCql.TODO_USER_ID;

import java.util.UUID;

import com.datastax.oss.driver.api.mapper.annotations.ClusteringColumn;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@CqlName(TABLE_TODOITEMS)
public class TodoItem {
    
    @PartitionKey
    @CqlName(TODO_USER_ID)
    private String userId;
    
    @ClusteringColumn
    @CqlName(TODO_ITEM_ID)
    private UUID itemId;
    
    @CqlName(TODO_TITLE)
    private String title;
    
    @CqlName(TODO_COMPLETED)
    private Boolean completed;
    
    @CqlName(TODO_OFFSET)
    private Integer offset;

}
