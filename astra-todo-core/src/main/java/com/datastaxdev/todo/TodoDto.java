package com.datastaxdev.todo;

import java.util.UUID;

import lombok.Data;

@Data
public class TodoDto {

    private String  userId;
    private UUID    itemId;
    private String  title;
    private Boolean completed = false;
    private Integer offset = 0;
    
    public TodoDto() {}
    
    public TodoDto(UUID itemid, String userid, String title) {
        this.itemId  = itemid;
        this.userId  = userid;
        this.title   = title;
    }
    
    
}
