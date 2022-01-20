package com.datastaxdev.todo;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.OPTIONS;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.datastaxdev.todo.web.Todo;

@RestController
@CrossOrigin(
  methods = {POST, GET, OPTIONS, PUT, DELETE, PATCH},
  maxAge = 3600,
  allowedHeaders = {"x-requested-with", "origin", "content-type", "accept"},
  origins = "*" 
)
@RequestMapping("/api/inmemory")
public class TodoRestControllerInMemory {
    
    private TodoItemRepository repo = new TodoItemRepositoryInMemory();
    
    @GetMapping("/{user}/todos/")
    public Stream<Todo> findAllByUser(HttpServletRequest req, 
            @PathVariable(value = "user") String user) {
        return repo.findByUser(user).stream()
                   .map(TodoUtils::mapAsTodo)
                   .map(t -> TodoUtils.setUrl(t, req));
    }
    
    @GetMapping("/{user}/todos/{uid}")
    public ResponseEntity<Todo> findById(HttpServletRequest req,
            @PathVariable(value = "user") String user,
            @PathVariable(value = "uid") String itemId) {
        Optional<TodoItem> e = repo.findById(user, UUID.fromString(itemId));
        if (e.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(TodoUtils.mapAsTodo(e.get()).setUrl(req.getRequestURL().toString()));
    }
     
    @PostMapping("/{user}/todos/")
    public ResponseEntity<Todo> create(HttpServletRequest req, 
            @PathVariable(value = "user") String user,
            @RequestBody Todo todoReq) 
    throws URISyntaxException {
        TodoItem te = TodoUtils.mapAsTodoEntity(todoReq, user);
        repo.save(te);
        todoReq.setUuid(te.getKey().getItemId());
        TodoUtils.setUrl(todoReq, req);
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
        TodoItem item = TodoUtils.mapAsTodoEntity(todoReq, user);
        repo.save(item);
        return ResponseEntity.accepted().body(todoReq);
    }
    
    @DeleteMapping("/{user}/todos/{uid}")
    public ResponseEntity<Void> deleteById(
            @PathVariable(value = "user") String user,
            @PathVariable(value = "uid")  String uid) {
        if (repo.findById(user, UUID.fromString(uid)).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        repo.deleteById(user, UUID.fromString(uid));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    @DeleteMapping("/{user}/todos/")
    public ResponseEntity<Void> deleteAllByUser(@PathVariable(value = "user") String user) {
        repo.deleteByUser(user);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    
}