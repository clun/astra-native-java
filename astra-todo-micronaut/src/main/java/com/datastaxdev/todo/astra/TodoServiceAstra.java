package com.datastaxdev.todo.astra;

import com.datastaxdev.todo.TodoDto;
import com.datastaxdev.todo.TodoService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the TodoService against Astra.
 *
 * @author Cedrick Lunven (@clunven)
 */
public class TodoServiceAstra implements TodoService {

    @Override
    public TodoDto save(TodoDto todo) {
        return null;
    }

    @Override
    public Optional<TodoDto> findById(String userId, UUID itemId) {
        return Optional.empty();
    }

    @Override
    public List<TodoDto> findByUser(String userId) {
        return null;
    }

    @Override
    public void deleteById(String userId, UUID itemId) {

    }

    @Override
    public void deleteByUser(String userId) {

    }

    @Override
    public void deleteAll() {

    }
}
