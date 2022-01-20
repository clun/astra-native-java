package com.datastaxdev.todo;

import java.util.List;
import java.util.stream.Collectors;

import com.datastaxdev.todo.web.Todo;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.validation.Validated;

@Validated
@Controller("/api/v1")
public class TodoRestController {
    
    private TodoServiceInMemory repo = new TodoServiceInMemory();
    
    @Get(value = "/{user}/todos/", produces = MediaType.APPLICATION_JSON)
    public List<Todo> findAllByUser(HttpRequest<?> httpRequest, 
          @PathVariable(value = "user") String user) {
        System.out.println(httpRequest.getPath());
          return repo.findByUser(user)
                   .stream()
                   .map(TodoRestController::mapAsTodo)
                   .collect(Collectors.toList());
                   //.map(t -> t.setUrl(req));
    }
    
    /*
    @GetMapping("/{user}/todos/{uid}")
    public ResponseEntity<Todo> findById(HttpServletRequest req,
            @PathVariable(value = "user") String user,
            @PathVariable(value = "uid") String itemId) {
        Optional<TodoItem> e = repo.findById(new TodoItemKey(user, UUID.fromString(itemId)));
        if (e.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(mapAsTodo(e.get()).setUrl(req.getRequestURL().toString()));
    }
     
    @PostMapping("/{user}/todos/")
    public ResponseEntity<Todo> create(HttpServletRequest req, 
            @PathVariable(value = "user") String user,
            @RequestBody Todo todoReq) 
    throws URISyntaxException {
        TodoItem te = mapAsTodoEntity(todoReq, user);
        repo.save(te);
        todoReq.setUuid(te.getKey().getItemId());
        todoReq.setUrl(req);
        return ResponseEntity.created(new URI(todoReq.getUrl())).body(todoReq);
    }
    
    @PatchMapping("/{user}/todos/{uid}")
    public ResponseEntity<Todo> update(HttpServletRequest req, 
            @PathVariable(value = "user") String user,
            @PathVariable(value = "uid") String uid, 
            @RequestBody Todo todoReq) 
    throws URISyntaxException {
        todoReq.setUuid(UUID.fromString(uid));
        todoReq.setUrl(req.getRequestURL().toString());
        TodoItem item = mapAsTodoEntity(todoReq, user);
        repo.save(item);
        return ResponseEntity.accepted().body(todoReq);
    }
    
    @DeleteMapping("/{user}/todos/{uid}")
    public ResponseEntity<Void> deleteById(
            @PathVariable(value = "user") String user,
            @PathVariable(value = "uid")  String uid) {
        if (!repo.existsById(new TodoItemKey(user, UUID.fromString(uid)))) {
            return ResponseEntity.notFound().build();
        }
        repo.deleteById(new TodoItemKey(user, UUID.fromString(uid)));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    @Delete("/{user}/todos/")
    public void deleteAllByUser(@PathVariable(value = "user") String user) {
        repo.deleteByKeyUserId(user);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    */
    
    private static Todo mapAsTodo(TodoDto te) {
        Todo todo = new Todo();
        todo.setTitle(te.getTitle());
        todo.setOrder(te.getOffset());
        todo.setUuid(te.getItemId());
        todo.setCompleted(te.getCompleted());
        return todo;
    }
    
    private TodoDto mapAsTodoEntity(Todo te, String user) {
        TodoDto todo = new TodoDto();
        if (null != te.getUuid()) {
            todo.setItemId(te.getUuid());
        }
        todo.setUserId(user);
        todo.setTitle(te.getTitle());
        todo.setOffset(te.getOrder());
        todo.setCompleted(te.isCompleted());
        return todo;
    }
}