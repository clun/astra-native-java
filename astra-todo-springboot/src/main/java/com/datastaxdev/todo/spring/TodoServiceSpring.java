package com.datastaxdev.todo.spring;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.datastaxdev.todo.TodoDto;
import com.datastaxdev.todo.TodoService;

@Repository("todo.service.spring-data")
public class TodoServiceSpring implements TodoService {

    @Autowired
    private TodoItemRepository springDataRepo;
    
    /** {@inheritDoc} */
    @Override
    public TodoDto save(TodoDto todo) {
        return toDto(springDataRepo.save(fromDto(todo)));
    }

    /** {@inheritDoc} */
    @Override
    public Optional<TodoDto> findById(String userId, UUID itemId) {
        return springDataRepo
                .findById(new TodoItemKey(userId, itemId))
                .map(this::toDto);
    }

    /** {@inheritDoc} */
    @Override
    public List<TodoDto> findByUser(String userId) {
        return springDataRepo
                .findByKeyUserId(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /** {@inheritDoc} */
    @Override
    public void deleteById(String userId, UUID itemId) {
        springDataRepo.deleteById(new TodoItemKey(userId, itemId));
    }

    /** {@inheritDoc} */
    @Override
    public void deleteByUser(String userId) {
        springDataRepo.deleteByKeyUserId(userId);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteAll() {
        springDataRepo.deleteAll();
    }
    
    /**
     * From Service to Repository
     * @param ti
     *      item
     * @return
     */
    public TodoItem fromDto(TodoDto ti) {
        TodoItem ts = new TodoItem();
        ts.setCompleted(ti.getCompleted());
        ts.setTitle(ti.getTitle());
        ts.setOffset(ti.getOffset());
        TodoItemKey tsk = new TodoItemKey();
        tsk.setItemId(ti.getItemId());
        if (tsk.getItemId() == null) {
            tsk.setItemId(Uuids.timeBased());
        }
        tsk.setUserId(ti.getUserId());
        ts.setKey(tsk);
        return ts;
    }
    
    public TodoDto toDto(TodoItem ts) {
        TodoDto ti = new TodoDto();
        ti.setCompleted(ts.isCompleted());
        ti.setItemId(ts.getKey().getItemId());
        ti.setUserId(ts.getKey().getUserId());
        ti.setOffset(ts.getOffset());
        ti.setTitle(ts.getTitle());
        return ti;
    }

}
