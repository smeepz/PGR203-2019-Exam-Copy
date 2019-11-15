package no.kristiania.dao;

import no.kristiania.dao.daos.*;
import no.kristiania.http.HttpServer;
import no.kristiania.http.controllers.*;
import org.flywaydb.core.Flyway;
import org.postgresql.ds.PGSimpleDataSource;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class TaskManagerServer {

    private final HttpServer server;

    public TaskManagerServer(int port) throws IOException {
        Properties properties = new Properties();
        try(FileReader fileReader = new FileReader("task-manager.properties")) {
            properties.load(fileReader);
        }

        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setURL(properties.getProperty("dataSource.url"));
        dataSource.setUser(properties.getProperty("dataSource.username"));
        dataSource.setPassword(properties.getProperty("dataSource.password"));

        Flyway flyway = Flyway.configure().dataSource(dataSource).load();
        flyway.migrate();

        ProjectDao projectDao = new ProjectDao(dataSource);
        TaskDao taskDao = new TaskDao(dataSource);
        UserDao userDao = new UserDao(dataSource);
        ProjectMemberDao projectMemberDao = new ProjectMemberDao(dataSource);
        TaskMemberDao taskMemberDao = new TaskMemberDao(dataSource);

        server = new HttpServer(port);
        server.setFileLocation("src/main/resources");
        server.addController("/api/projects", new ProjectsController(projectDao));
        server.addController("/api/tasks", new TaskController(taskDao));
        server.addController("/api/taskStatus", new TaskStatusController(taskDao));
        server.addController("/api/users", new UsersController(userDao));
        server.addController("/api/projectMembers", new ProjectMembersController(projectMemberDao, userDao));
        server.addController("/api/taskMembers", new TaskMembersController(taskMemberDao, userDao));

        /*TODO:
        -- Adding new projects
        -- Adding new tasks
        -- Changing task status (Freely)
        -- Assigning members to task (Using options)
        -- Filtering on individual member tasks
        -- Assigning members to project (Using options)
         */
    }

    public static void main(String[] args) throws IOException {
        new TaskManagerServer(8080).start();
    }

    private void start() throws IOException {
        server.start();
    }
}
