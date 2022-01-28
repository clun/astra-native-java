package com.datastaxdev.todo;

import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TodoDto {

    private String  userId;
    private UUID    itemId;
    private String  title;
    private Boolean completed;
    private Integer offset;
    
}
