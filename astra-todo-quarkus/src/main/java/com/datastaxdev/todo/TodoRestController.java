package com.datastaxdev.todo;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.reactive.server.spi.ServerRequestContext;

import com.datastax.oss.quarkus.runtime.api.session.QuarkusCqlSession;
import com.datastaxdev.todo.cassandra.TodoServiceCassandraCql;
import com.datastaxdev.todo.cassandra.TodoServicesCassandraOM;
import com.datastaxdev.todo.web.Todo;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.PathVariable;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.common.annotation.Blocking;
import io.vertx.core.spi.observability.HttpRequest;

/**
 * Expose the todo Interface.
 *
 * @author Cedrick LUNVEN (@clunven)
 *
 * @see https://quarkus.io/guides/rest-json
 */
@ApplicationScoped
@Path("/api/v1")
public class TodoRestController {
    
    @Inject
    private QuarkusCqlSession cqlSession;
    
    @ConfigProperty(name = "todo.persistence", defaultValue = TodoService.PERSISTENCE_INMEMORY)
    private String persistence;
    
    @ConfigProperty(name = "todo.cassandra.create_schema", defaultValue="false")
    private boolean createTable;
    
    /** Todo service reference. */
    private TodoService todoService;
   
    /**
     * Initialized at beginning of the Application.
     *
     * @param ev
     *      startup event
     * @return
     *      if the operation is applied
     */
    public boolean onStart(@Observes StartupEvent ev){
        if (createTable) {
            TodoServiceCassandraCql.createTableTodo(cqlSession);
        }
        return createTable;
    }

    @GET
    @Path("/{user}/todos/")
    @Blocking
    @Produces(MediaType.APPLICATION_JSON)
    public List<Todo> findAllByUser(
            @Context HttpRequest req,
            @PathParam(value = "user") String user) {
        return getTodoService().findByUser(user)
                   .stream()
                   .map(this::fromDto)
                   .map(t -> populateUrlWithId(t, req))
                   .collect(Collectors.toList());
    }
    
    @GET
    @Path("/{user}/todos/{uid}")
    @Blocking
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(
            @Context HttpRequest req,
            @PathParam(value = "user") String user,
            @PathParam(value = "uid")  String itemId) {
        Optional<TodoDto> e = getTodoService().findById(user, UUID.fromString(itemId));
        if (e.isEmpty()) return Response.status(Status.NOT_FOUND).build();
        Todo todo = fromDto(e.get());
        populateUrl(todo, req);
        return Response.ok(todo).build();
    }

    @POST
    @Path("/{user}/todos/")
    @Blocking
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(
            @Context HttpRequest req,
            @PathParam(value = "user") String user,
            @Body Todo todoReq) throws URISyntaxException {
        TodoDto te = toDto(todoReq, user);
        te = getTodoService().save(te);
        todoReq.setUuid(te.getItemId());
        populateUrlWithId(todoReq, req);
        //LOGGER.info("Created user={}, TODO={}", user, todoReq);
        return Response.created(new URI(todoReq.getUrl())).body(todoReq);
    }
    
    /**
     * Access todo service.
     *
     * @return
     *      todo services
     */
    public TodoService getTodoService() {
        if (todoService == null) {
            if (TodoService.PERSISTENCE_INMEMORY.equals(persistence)) {
                todoService = new TodoServiceInMemory();
            } else if (TodoService.PERSISTENCE_CASSANDRA_CQL.equals(persistence)) {
                todoService = new TodoServiceCassandraCql(cqlSession);
            } else if (TodoService.PERSISTENCE_CASSANDRA_OM.equals(persistence)) {
                todoService = new TodoServicesCassandraOM(cqlSession);
            }
        }
        return todoService;
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

    private Todo populateUrlWithId(Todo t, HttpRequest req) {
        
        String fullUrl = new StringBuilder(req.absoluteURI())
                .append(t.getUuid().toString())
                .toString();
        if (fullUrl.contains("gitpod")) {
            fullUrl.replaceAll("http://", "https://");
        }
        t.setUrl(fullUrl);
        return t;
    }

    private Todo populateUrl(Todo t, HttpRequest req) {
        String fullUrl = req.absoluteURI();
        if (fullUrl.contains("gitpod")) {
            fullUrl.replaceAll("http://", "https://");
        }
        t.setUrl(fullUrl);
        return t;
    }
    
}
