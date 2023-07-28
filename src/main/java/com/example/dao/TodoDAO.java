package com.example.dao;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import com.example.model.Todo;

import java.util.List;

public class TodoDAO {

    private EntityManager em;

    public TodoDAO() {
        em = Persistence.createEntityManagerFactory("com.example.dao.TodoPU").createEntityManager();
    }

    public List<Todo> getAllTodos() {
        return em.createQuery("SELECT t FROM Todo t", Todo.class).getResultList();
    }

    public Todo getTodo(Long id) {
        return em.find(Todo.class, id);
    }

    public Todo createTodo(Todo todo) {
        em.getTransaction().begin();
        em.persist(todo);
        em.getTransaction().commit();
        return todo;
    }

    public void deleteTodo(Long id) {
        Todo todo = getTodo(id);
        if (todo != null) {
            em.getTransaction().begin();
            em.remove(todo);
            em.getTransaction().commit();
        }
    }
}
