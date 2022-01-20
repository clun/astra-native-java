package com.datastaxdev;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.datastaxdev.todo.spring.TodoItemRepository;
import com.datastaxdev.todo.spring.TodoItem;
import com.datastaxdev.todo.spring.TodoItemKey;

@SpringBootTest
class Test03_SaveTask {

    @Autowired
    private TodoItemRepository repo;
    
	@Test
	void shoud_save_task_when_save() {
	    // Given
	    TodoItem te = new TodoItem("john", "Sample task1");
	    // When
	    repo.save(te);
	    // then
	    Assertions.assertTrue(repo.existsById(new TodoItemKey("john", te.getKey().getItemId())));
	}

}
