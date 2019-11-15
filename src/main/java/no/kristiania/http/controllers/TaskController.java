package no.kristiania.http.controllers;

import no.kristiania.dao.daos.TaskDao;

import java.sql.SQLException;
import java.util.stream.Collectors;

public class TaskController extends AbstractDaoController {
    private final TaskDao taskDao;

    public TaskController(TaskDao taskDao) {
        this.taskDao = taskDao;
    }


    public String getBody() throws SQLException {
        String urlQuery = super.getUrlQuery();
        long projectId = Long.parseLong(urlQuery.substring(urlQuery.indexOf('=')+1));
        return taskDao.listTasksOfProject(projectId).stream()
                .map(t -> String.format("<li id='%s'><a href='task.html?projectid=%s&taskid=%s'>%s</a></li>", t.getId(), t.getProjectId(), t.getId(), t.getName()))
                .collect(Collectors.joining(""));
    }

    public void setUrlQuery(String urlQuery){
        super.setUrlQuery(urlQuery);
    }
}
