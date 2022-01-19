package com.datastaxdev.todo;

import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.datastaxdev.todo.web.CreateTodoCommand;
import com.datastaxdev.todo.web.Todo;
import com.datastaxdev.todo.web.UpdateTodoCommand;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Options;
import io.micronaut.http.annotation.Patch;
import io.micronaut.http.annotation.Post;

public interface TodoOperations {

    @Get("/")
    Iterable<Todo> findAll();

    @Get("/{id}")
    HttpResponse<Todo> findById(@NotNull UUID id);

    @Post
    HttpResponse<Todo> create(@Body @NotNull @Valid CreateTodoCommand body);

    @Patch("/{id}")
    HttpResponse<Todo> updateById(@NotNull UUID id, @Body @NotNull @Valid UpdateTodoCommand body);

    @Delete("/")
    HttpResponse<String> deleteAll();

    @Delete("/{id}")
    HttpResponse<String> deleteById(@NotNull UUID id);

    @Options("/")
    HttpResponse<String> options();
}
