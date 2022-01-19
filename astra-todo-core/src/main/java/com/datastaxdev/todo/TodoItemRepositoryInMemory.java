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
public class TodoItemRepositoryInMemory implements TodoItemRepository {
    
    /** Map < UserId, Map< ItemId, Todo > >. */
    private Map<String, Map<UUID, TodoItem> > todoStore = new ConcurrentHashMap<>();
    
    public TodoItemRepositoryInMemory() {
        sampleDatas();
    }
    
    public void sampleDatas() {
        save(new TodoItem("john", "task_1"));
        save(new TodoItem("john", "task_2"));
        save(new TodoItem("john", "task_3"));
        save(new TodoItem("mary", "xxx"));
    }
    
    /** {@inheritDoc} */
    public TodoItem save(TodoItem todo) {
        assertNotNull(todo);
        assertNotEmpty(todo.getUserId());
        if (null == todo.getItemId()) {
            todo.setItemId(UUID.randomUUID());
        }
        if (!todoStore.containsKey(todo.getUserId())) {
            todoStore.put(todo.getUserId(), new HashMap<UUID, TodoItem>());
        } 
        todoStore.get(todo.getUserId()).put(todo.getItemId(), todo);
        return todo;
    }

    /** {@inheritDoc} */
    public Optional<TodoItem> findById(String userId, UUID itemId) {
        assertNotNull(itemId);
        assertNotEmpty(userId);
        return todoStore.containsKey(userId) ? 
                Optional.ofNullable(todoStore.get(userId).get(itemId)) : 
                Optional.empty();
    }
    
    /** {@inheritDoc} */
    public List<TodoItem> findByUser(String userId) {
        assertNotEmpty(userId);
        return todoStore.containsKey(userId) ? 
                new ArrayList<TodoItem>(todoStore.get(userId).values()) : 
                new ArrayList<TodoItem>();
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
