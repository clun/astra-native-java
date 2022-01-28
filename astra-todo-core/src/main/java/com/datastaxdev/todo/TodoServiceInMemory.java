package com.datastaxdev.todo;

import static com.datastaxdev.utils.ValidationUtils.assertNotEmpty;
import static com.datastaxdev.utils.ValidationUtils.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of a multi-user todolist in Memory
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class TodoServiceInMemory implements TodoService {
    
    /** Map < UserId, Map< ItemId, Todo > >. */
    private Map<String, Map<UUID, TodoDto> > todoStore = new ConcurrentHashMap<>();
    
    public TodoServiceInMemory() {
    }
    
    /** {@inheritDoc} */
    public TodoDto save(TodoDto todo) {
        assertNotNull(todo);
        assertNotEmpty(todo.getUserId());
        if (null == todo.getItemId()) {
            todo.setItemId(UUID.randomUUID());
        }
        if (!todoStore.containsKey(todo.getUserId())) {
            todoStore.put(todo.getUserId(), new HashMap<UUID, TodoDto>());
        } 
        todoStore.get(todo.getUserId()).put(todo.getItemId(), todo);
        return todo;
    }

    /** {@inheritDoc} */
    public Optional<TodoDto> findById(String userId, UUID itemId) {
        assertNotNull(itemId);
        assertNotEmpty(userId);
        return todoStore.containsKey(userId) ? 
                Optional.ofNullable(todoStore.get(userId).get(itemId)) : 
                Optional.empty();
    }
    
    /** {@inheritDoc} */
    public List<TodoDto> findByUser(String userId) {
        assertNotEmpty(userId);
        return todoStore.containsKey(userId) ? 
                new ArrayList<TodoDto>(todoStore.get(userId).values()) : 
                new ArrayList<TodoDto>();
    }

    /** {@inheritDoc} */
    public void deleteById(String userId, UUID itemId) {
        assertNotNull(itemId);
        assertNotEmpty(userId);
        if (todoStore.containsKey(userId)) {
            todoStore.get(userId).remove(itemId);
        }
    }

    /** {@inheritDoc} */
    public void deleteByUser(String userId) {
        assertNotEmpty(userId);
        todoStore.remove(userId); 
    }

    /** {@inheritDoc} */
    public void deleteAll() {
        todoStore.clear();
    }
    
}
