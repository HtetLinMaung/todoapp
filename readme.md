# REST API using JAX-RS

This tutorial will guide you through the steps to create a simple RESTful API for a "Todo" application using JAX-RS and PostgreSQL as the database. For simplicity, we'll use a simple `Todo` object with two properties: `id` and `description`.

Before we start, make sure you have the following installed:

- JDK 1.8 or above
- Maven
- PostgreSQL server
- Postman or curl (for testing the API)

## Create a new Maven project

You can do this in your preferred IDE or from the command line. If you are using command line, navigate to the directory where you want to create your project and run:

```bash
mvn archetype:generate -DgroupId=com.example -DartifactId=todoapp -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false
```

Replace `com.example` and `todoapp` with your desired group ID and project name respectively.

## Add dependencies to your pom.xml

You will need to add the following dependencies:

```xml
<!-- JAX-RS -->
<dependency>
    <groupId>org.glassfish.jersey.containers</groupId>
    <artifactId>jersey-container-servlet</artifactId>
    <version>3.1.3</version>
</dependency>
<dependency>
    <groupId>org.glassfish.jersey.inject</groupId>
    <artifactId>jersey-hk2</artifactId>
    <version>3.1.3</version>
</dependency>

<!-- PostgreSQL JDBC driver -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.6.0</version>
</dependency>

<!-- JSON support -->
<dependency>
    <groupId>org.glassfish.jersey.media</groupId>
    <artifactId>jersey-media-json-jackson</artifactId>
    <version>3.1.3</version>
</dependency>

<!-- JPA and Hibernate -->
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-core</artifactId>
    <version>6.3.0.CR1</version>
</dependency>
<dependency>
    <groupId>javax.persistence</groupId>
    <artifactId>javax.persistence-api</artifactId>
    <version>2.2</version>
</dependency>

<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.28</version>
</dependency>
```

## Configure your database

You will need to have a PostgreSQL database set up and available. In this tutorial, we will create a simple `todos` table with columns `id` and `description`.

```sql
CREATE TABLE todos (
    id SERIAL PRIMARY KEY,
    description VARCHAR(255)
);
```

## Create the Todo entity

Create a new class `Todo` in your project with the following code:

```java
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Todo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;
}
```

## Create the TodoDAO class

This class will be used to interact with the database. We will use the Hibernate ORM to simplify this process.

```java
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.util.List;

public class TodoDAO {

    private EntityManager em;

    public TodoDAO() {
        em = Persistence.createEntityManagerFactory("com.example.TodoPU").createEntityManager();
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
```

## Create the TodoResource class

This class will define the RESTful API endpoints.

```java
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

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
```

## Create a JAX-RS Application class

This class will be used to register the `TodoResource`.

```java
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
public class TodoApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(TodoResource.class);
        return classes;
    }
}
```

In the above code, `@ApplicationPath("/api")` sets the base URI for all resource URIs provided by the application. `getClasses()` method is overridden to return a set of all resource classes — in this case, just `TodoResource`.

## Create a persistence.xml file

To connect to the database using Hibernate, you need a persistence.xml configuration file. This file should be located in the `src/main/resources/META-INF` directory.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence xmlns="http://xmlns.jcp.org/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence http://xmlns.jcp.org/xml/ns/persistence/persistence_2_2.xsd"
             version="2.2">
    <persistence-unit name="com.example.TodoPU" transaction-type="RESOURCE_LOCAL">
        <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>
        <exclude-unlisted-classes>false</exclude-unlisted-classes>
        <properties>
            <property name="javax.persistence.jdbc.url" value="jdbc:postgresql://localhost:5432/yourDatabaseName"/>
            <property name="javax.persistence.jdbc.user" value="yourUsername"/>
            <property name="javax.persistence.jdbc.password" value="yourPassword"/>
            <property name="hibernate.hbm2ddl.auto" value="update"/>
            <property name="hibernate.dialect" value="org.hibernate.dialect.PostgreSQLDialect"/>
        </properties>
    </persistence-unit>
</persistence>
```

In the above code, make sure to replace `yourDatabaseName`, `yourUsername`, and `yourPassword` with your actual PostgreSQL database name, username, and password.

## Test the API

Now you should be able to test your API using a tool like Postman or curl. Run your server and you should be able to make requests to `http://localhost:8080/api/todos`.

Remember that this is a simple application and may not include all the best practices for a production-level application. It lacks exception handling, validation, and security features like authentication and authorization, which are essential for any real-world application.
