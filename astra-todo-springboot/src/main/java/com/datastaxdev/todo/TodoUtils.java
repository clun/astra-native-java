package com.datastaxdev.todo;

import javax.servlet.http.HttpServletRequest;

import com.datastaxdev.todo.web.Todo;

public class TodoUtils {
    
    public static Todo mapAsTodo(TodoItem te) {
        Todo todo = new Todo();
        todo.setTitle(te.getTitle());
        todo.setOrder(te.getOffset());
        todo.setUuid(te.getKey().getItemId());
        todo.setCompleted(te.isCompleted());
        return todo;
    }
    
    public static TodoItem mapAsTodoEntity(Todo te, String user) {
        TodoItem todo = new TodoItem();
        if (null != te.getUuid()) {
            todo.getKey().setItemId(te.getUuid());
        }
        todo.getKey().setUserId(user);
        todo.setTitle(te.getTitle());
        todo.setOffset(te.getOrder());
        todo.setCompleted(te.isCompleted());
        return todo;
    }
    
    public static Todo setUrl(Todo t, HttpServletRequest req) {
        if (t.getUrl() == null) {
            String reqUrl = req.getRequestURL().toString();
            String url = reqUrl.contains("gitpod") ? reqUrl.replaceAll("http", "https") : reqUrl;
            url += t.getUuid();
            t.setUrl(url);
        }
        return t;
    }
    
    public static Todo setUrl(Todo t, String myUrl) {
        if (t.getUrl() == null) {
            t.setUrl(myUrl.contains("gitpod") ? myUrl.replaceAll("http", "https") : myUrl);
        }
        return t;
    }

}
