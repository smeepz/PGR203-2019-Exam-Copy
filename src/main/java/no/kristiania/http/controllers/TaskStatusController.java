package no.kristiania.http.controllers;

import no.kristiania.dao.daos.TaskDao;
import no.kristiania.http.HttpMessage;
import no.kristiania.http.HttpRequest;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Map;
import java.util.stream.Collectors;

public class TaskStatusController extends AbstractDaoController {
    private final TaskDao taskDao;

    public TaskStatusController(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

    @Override
    public void handle(String requestAction, String requestTarget, Map<String, String> query, String body, OutputStream out) throws IOException {
        setUrlQuery(HttpMessage.getQueryString(requestTarget));
        try {
            if(requestAction.equals("POST")){
                body = URLDecoder.decode(body, StandardCharsets.UTF_8);
                query = HttpMessage.parseQueryString(body);
                setUrlQuery(HttpMessage.getQueryString(body));
                taskDao.updateTaskStatus(query.get("taskStatus"), Long.parseLong(query.get("taskid")));
            }
            serverDaoResponse(query, out);
        } catch (SQLException e) {
            serverErrorResponse(out,e);
        }
    }

    public String getBody() throws SQLException {
        String urlQuery = super.getUrlQuery();
        String taskLine = urlQuery.split("&")[1];
        long taskId = Long.parseLong(taskLine.substring(taskLine.indexOf('=')+1));
        return taskDao.getTaskFromId(taskId).stream()
                .map(t -> String.format("<a id='%s' href=setStatus.html?projectid=%s&taskid=%s>%s</a> <= Click to change", t.getId(), t.getProjectId(), t.getId(), t.getStatus()))
                .collect(Collectors.joining(""));
    }

    public void setUrlQuery(String urlQuery){
        super.setUrlQuery(urlQuery);
    }
}
