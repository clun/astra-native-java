package com.datastaxdev.todo;

import com.datastaxdev.todo.web.Todo;
import com.datastaxdev.utils.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@CrossOrigin(
  methods = {POST, GET, OPTIONS, PUT, DELETE, PATCH},
  maxAge = 3600,
  allowedHeaders = {"x-requested-with", "origin", "content-type", "accept"},
  origins = "*" 
)
@RequestMapping("/api/v1")
public class TodoRestController {
    
    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(TodoRestController.class);
    
    @Autowired
    @Qualifier("todo.service.inmemory")
    //@Qualifier("todo.service.spring-data")
    private TodoService repo;
    
    @GetMapping("/{user}/todos/")
    public Stream<Todo> findAllByUser(
            HttpServletRequest req, 
            @PathVariable(value = "user") String user) {
        LOGGER.info("Find all user={}", user);
        ValidationUtils.assertNotEmpty(user);
        return repo.findByUser(user).stream()
                   .map(this::fromDto)
                   .map(t -> updateUrl(t, req));
    }
    
    @GetMapping("/{user}/todos/{uid}")
    public ResponseEntity<Todo> findById(HttpServletRequest req,
            @PathVariable(value = "user") String user,
            @PathVariable(value = "uid") String itemId) {
        LOGGER.info("Find user={}, id={}", user, itemId);
        Optional<TodoDto> e = repo.findById(user, UUID.fromString(itemId));
        if (e.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Todo todo = fromDto(e.get());
        updateUrl(todo, req);
        return ResponseEntity.ok(todo);
    }
     
    @PostMapping("/{user}/todos/")
    public ResponseEntity<Todo> create(HttpServletRequest req, 
            @PathVariable(value = "user") String user,
            @RequestBody Todo todoReq) 
    throws URISyntaxException {
        LOGGER.info("Create user={}, TODO={}", user, todoReq);
        TodoDto te = toDto(todoReq, user);
        te = repo.save(te);
        todoReq.setUuid(te.getItemId());
        updateUrl(todoReq, req);
        return ResponseEntity.created(new URI(todoReq.getUrl())).body(todoReq);
    }
    
    @PatchMapping("/{user}/todos/{uid}")
    public ResponseEntity<Todo> update(HttpServletRequest req, 
            @PathVariable(value = "user") String user,
            @PathVariable(value = "uid") String uid, 
            @RequestBody Todo todoReq) 
    throws URISyntaxException {
        LOGGER.info("Updating user={} it [] with TODO {}", user, uid, todoReq);
        todoReq.setUuid(UUID.fromString(uid));
        todoReq.setUrl(req.getRequestURL().toString());
        TodoDto item = toDto(todoReq, user);
        repo.save(item);
        return ResponseEntity.accepted().body(todoReq);
    }
    
    @DeleteMapping("/{user}/todos/{uid}")
    public ResponseEntity<Void> deleteById(
            @PathVariable(value = "user") String user,
            @PathVariable(value = "uid")  String uid) {
        LOGGER.info("Delete TODO id {} for user {}", uid, user);
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
    
    private Todo fromDto(TodoDto te) {
        Todo todo = new Todo();
        todo.setTitle(te.getTitle());
        todo.setOrder(te.getOffset());
        todo.setUuid(te.getItemId());
        todo.setCompleted(te.getCompleted());
        return todo;
    }
    
    private TodoDto toDto(Todo te, String user) {
        TodoDto todo = new TodoDto();
        todo.setItemId(te.getUuid());
        todo.setUserId(user);
        todo.setTitle(te.getTitle());
        todo.setOffset(te.getOrder());
        todo.setCompleted(te.isCompleted());
        return todo;
    }
    
    private Todo updateUrl(Todo t, HttpServletRequest req) {
        String reqUrl = req.getRequestURL().toString();
        String url = reqUrl.contains("gitpod") ? reqUrl.replaceAll("http", "https") : reqUrl;
        t.setUrl(url + t.getUuid());
        return t;
    }
    
}