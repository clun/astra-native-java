package com.datastaxdev.todo;

import io.micronaut.http.annotation.Controller;
import io.micronaut.validation.Validated;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Validated
@Controller("/todos")
public class TodoController /*implements TodoOperations*/ {

    /*
    private final TodoItemRepositoryInMemory todoRepository;
    
    private final HttpHostResolver httpHostResolver;

    @Get("/")
    public Iterable<TodoDTO> findAll() {
        return todoMapper.toDTO(todoRepository.findAll());
    }

    @Override
    public HttpResponse<TodoDTO> findById(@NotNull UUID id) {
        Optional<TodoItem> existingTodo = todoRepository.findById(id);
        if (existingTodo.isPresent()) {
            return HttpResponse.ok(todoMapper.toDTO(existingTodo.get()));
        } else {
            return HttpResponse.notFound();
        }
    }

    @Override
    public HttpResponse<TodoDTO> create(@Body @NotNull @Valid CreateTodoCommand body) {
        HttpRequest<Object> httpRequest = ServerRequestContext.currentRequest()
                .orElseThrow(() -> new RuntimeException("no request context available"));
        String basePath = httpHostResolver.resolve(httpRequest) + httpRequest.getPath();
        TodoItem todo = todoMapper.fromCreateCommand(body, basePath);
        TodoItem result = todoRepository.save(todo);
        return HttpResponse.created(todoMapper.toDTO(result), createdUri(httpRequest, result));
    }

    @Override
    public HttpResponse<TodoDTO> updateById(@NotNull UUID id, @Body @NotNull @Valid UpdateTodoCommand body) {
        Optional<TodoItem> existingTodo = todoRepository.findById(id);
        if (existingTodo.isPresent()) {
            TodoItem todo = todoMapper.fromUpdateCommand(body, existingTodo.get());
            TodoItem result = todoRepository.update(todo);
            return HttpResponse.ok(todoMapper.toDTO(result));
        } else {
            return HttpResponse.notFound();
        }
    }

    @Override
    public HttpResponse<String> deleteAll() {
        todoRepository.deleteAll();
        return HttpResponse.noContent();
    }

    @Override
    public HttpResponse<String> deleteById(@NotNull UUID id) {
        todoRepository.deleteById(id);
        return HttpResponse.noContent();
    }

    @Override
    public HttpResponse<String> options() {
        return HttpResponse.ok();
    }

    private URI createdUri(HttpRequest<?> http, TodoItem todo) {
        return UriBuilder.of(http.getUri()).path(todo.getId().toString()).build();
    }

    @Error(status = HttpStatus.NOT_FOUND)
    public HttpResponse<JsonError> notFound(HttpRequest<?> request) {
        JsonError error = new JsonError("Todo Not Found").link(Link.SELF, Link.of(request.getUri()));
        return HttpResponse.<JsonError>notFound().body(error);
    }
*/
}
