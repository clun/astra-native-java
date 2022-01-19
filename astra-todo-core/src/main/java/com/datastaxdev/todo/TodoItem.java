package com.datastaxdev.todo;

import java.util.UUID;

import lombok.Data;

@Data
public class TodoItem {

    private String userId;
    private UUID itemId = UUID.randomUUID();
    private String title;
    private Boolean completed = false;
    private Integer offset = 0;
    
    public TodoItem() {}
    
    public TodoItem(String userid, String title) {
        this.userId     = userid;
        this.title      = title;
    }

}
