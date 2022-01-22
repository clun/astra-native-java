package com.datastaxdev.todo.astra;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * For Basic operations you can leverage on Interface only repository
 */
@Repository
public interface TodoItemRepository extends CassandraRepository<TodoItem, TodoItemKey> {
    
    /**
     * Retrieve all todos for a user as the userId is the partition key.
     *
     * @param userid
     *      unique identifier for a user
     * @return
     */
    List<TodoItem> findByKeyUserId(String userid);
    
    /**
     * Delete all tasks for a todolist.
     * 
     * @param userid
     *          current userid.
     */
    void deleteByKeyUserId(String userid);
    
}
