package com.example.resource;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

import com.example.dao.TodoDAO;
import com.example.model.Todo;

@Path("/todos")
@Produces(MediaType.APPLICATION_JSON)
public class TodoResource {

    private TodoDAO dao = new TodoDAO();

    @GET
    public List<Todo> getAllTodos() {
        return dao.getAllTodos();
    }

    @GET
    @Path("/{id}")
    public Todo getTodo(@PathParam("id") Long id) {
        return dao.getTodo(id);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Todo createTodo(Todo todo) {
        return dao.createTodo(todo);
    }

    @DELETE
    @Path("/{id}")
    public void deleteTodo(@PathParam("id") Long id) {
        dao.deleteTodo(id);
    }
}
