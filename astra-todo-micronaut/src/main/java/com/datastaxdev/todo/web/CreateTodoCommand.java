package com.datastaxdev.todo.web;

import io.micronaut.core.annotation.Introspected;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
@Introspected
public class CreateTodoCommand {

    @NotBlank
    private String title;
    
    private Boolean completed;
    
    private Long order;

}
