package com.datastaxdev.todo;

import com.datastaxdev.todo.web.Todo;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.http.context.ServerRequestContext;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.validation.Validated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Validated
@Controller("/api/v1")
public class TodoRestController {
    
    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(TodoRestController.class);
    
    private TodoService repo = new TodoServiceInMemory();
    
    @Get(value = "/{user}/todos/", produces = MediaType.APPLICATION_JSON)
    public List<Todo> findAllByUser(
            @PathVariable(value = "user") @NotEmpty  String user) {
        return repo.findByUser(user)
                   .stream()
                   .map(this::fromDto)
                   .map(t -> updateUrlWithId(t, ServerRequestContext.currentRequest().get()))
                   .collect(Collectors.toList());
    }

    @Get("/{user}/todos/{uid}")
    public HttpResponse<Todo> findById(
            @PathVariable(value = "user") @NotEmpty String user,
            @PathVariable(value = "uid")  @NotEmpty String itemId) {
        Optional<TodoDto> e = repo.findById(user, UUID.fromString(itemId));
        if (e.isEmpty()) return HttpResponse.notFound();
        Todo todo = fromDto(e.get());
        updateUrl(todo, ServerRequestContext.currentRequest().get());
        return HttpResponse.ok(todo);
    }

    @Post("/{user}/todos/")
    public HttpResponse<Todo> create(
            @PathVariable(value = "user") String user,
            @Body @NotNull Todo todoReq) throws URISyntaxException {
        LOGGER.info("Create user={}, TODO={}", user, todoReq);
        TodoDto te = toDto(todoReq, user);
        te = repo.save(te);
        todoReq.setUuid(te.getItemId());
        updateUrlWithId(todoReq, ServerRequestContext.currentRequest().get());
        return HttpResponse.created(new URI(todoReq.getUrl())).body(todoReq);
    }

    @Patch("/{user}/todos/{uid}")
    public HttpResponse<Todo> update(
            @PathVariable(value = "user")  @NotEmpty String user,
            @PathVariable(value = "uid") @NotEmpty  String itemId,
            @Body @NotNull Todo todo)
    throws URISyntaxException {
        LOGGER.info("Updating user={} id {} with TODO {}", user, itemId, todo);
        Optional<TodoDto> e = repo.findById(user, UUID.fromString(itemId));
        if (e.isEmpty()) return HttpResponse.notFound();
        todo.setUuid(UUID.fromString(itemId));
        TodoDto todoDto = toDto(todo, user);
        repo.save(todoDto);
        updateUrl(todo, ServerRequestContext.currentRequest().get());
        return HttpResponse.ok(todo);
    }
    
    @Delete("/{user}/todos/{uid}")
    public HttpResponse<Void> deleteById(
            @PathVariable(value = "user") String user,
            @PathVariable(value = "uid")  String uid) {
        LOGGER.info("Delete TODO id {} for user {}", uid, user);
        if (repo.findById(user, UUID.fromString(uid)).isEmpty()) {
            return HttpResponse.notFound();
        }
        repo.deleteById(user, UUID.fromString(uid));
        return HttpResponse.noContent();
    }
    
    @Delete("/{user}/todos/")
    public HttpResponse<Void> deleteAllByUser(@PathVariable(value = "user") String user) {
        repo.deleteByUser(user);
        return HttpResponse.noContent();
    }

    private Todo fromDto(TodoDto te) {
        Todo todo = new Todo();
        todo.setTitle(te.getTitle());
        todo.setOrder(te.getOffset());
        todo.setUuid(te.getItemId());
        todo.setCompleted(te.getCompleted());
        return todo;
    }
    
    private TodoDto toDto(Todo te, String user) {
        TodoDto dto = new TodoDto();
        dto.setUserId(user);
        dto.setItemId(te.getUuid());
        dto.setTitle(te.getTitle());
        dto.setOffset(te.getOrder());
        dto.setCompleted(te.isCompleted());
        return dto;
    }

    /**
     * Update the todo web bean with its url (HateOAS).
     * @param t
     *      todo value
     * @param req
     *      current http request
     * @return
     *      value
     */
    private Todo updateUrlWithId(Todo t, HttpRequest<Object> req) {
        String fullUrl = new StringBuilder("http://")
                .append(req.getServerAddress().getHostName())
                .append(":")
                .append(req.getServerAddress().getPort())
                .append(UriBuilder.of(req.getUri()).path(t.getUuid().toString()))
                .toString();
        if (fullUrl.contains("gitpod")) {
            fullUrl.replaceAll("http://", "https://");
        }
        t.setUrl(fullUrl);
        return t;
    }

    /**
     * Update the todo web bean with its url (HateOAS).
     * @param t
     *      todo value
     * @param req
     *      current http request
     * @return
     *      value
     */
    private Todo updateUrl(Todo t, HttpRequest<Object> req) {
        String fullUrl = new StringBuilder("http://")
                .append(req.getServerAddress().getHostName())
                .append(":")
                .append(req.getServerAddress().getPort())
                .append(UriBuilder.of(req.getUri()))
                .toString();
        if (fullUrl.contains("gitpod")) {
            fullUrl.replaceAll("http://", "https://");
        }
        t.setUrl(fullUrl);
        return t;
    }
}