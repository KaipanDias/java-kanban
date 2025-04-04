package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;
import model.Status;
import model.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TaskHttpHandler  extends BaseHttpHandler implements HttpHandler {
    public TaskHttpHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        switch (httpExchange.getRequestMethod()) {
            case "GET":
                handleGet(httpExchange);
                break;
            case "POST":
                handlePost(httpExchange);
                break;
            case "DELETE":
                handleDelete(httpExchange);
                break;
            default:
                sendNotFound(httpExchange, "Такого эндпойнта не существует");
        }
    }


    private void handleGet(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        String response;

        Pattern taskPattern = Pattern.compile("^/tasks/(\\d+)$");
        Matcher taskMatcher = taskPattern.matcher(path);

        if (path.matches("/tasks")) {
            response = gson.toJson(taskManager.getTasks());
            sendText(httpExchange, response);
            System.out.println(response);
        } else if (taskMatcher.matches()) {
            int id = Integer.parseInt(taskMatcher.group(1));
            if (taskManager.getTaskById(id) != null){
                response = gson.toJson(taskManager.getTaskById(id));
                sendText(httpExchange, response);
            }else{
                sendNotFound(httpExchange, "Задача с таким ID не найдена");
            }
        } else {
            sendNotFound(httpExchange, "Такого пути нет");
        }
    }

    private void handlePost(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();

        String requestBody = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Task task = gson.fromJson(requestBody, Task.class);

        if (task == null){
            sendMessage(httpExchange, "Тело запроса пустое", 400);
            return;
        }
        if (path.matches("/tasks")) {
            int id = task.getId();

            if (id != 0){
                taskManager.updateTask(task);
                sendMessage(httpExchange, "Задача успешно обновлена", 201);
            }else{
                int newTaskId = taskManager.addNewTask(task);

                if(newTaskId == 0){
                    sendMessage(httpExchange, "Задача пересекается с существуещей", 406);
                }else {
                    sendMessage(httpExchange,"Задача успешно добавлена", 201);
                }
            }
        } else {
            sendNotFound(httpExchange, "Такого пути нет");
        }
    }

    private void handleDelete(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();

        Pattern taskPattern = Pattern.compile("^/tasks/(\\d+)$");
        Matcher taskMatcher = taskPattern.matcher(path);

        if (path.matches("/tasks")) {
            taskManager.deleteAllTasks();
            sendText(httpExchange, "Все задачи удалены");
        } else if (taskMatcher.matches()) {
            int id = Integer.parseInt(taskMatcher.group(1));
            if (taskManager.getTaskById(id) != null){
                taskManager.deleteTaskById(id);
                sendText(httpExchange, "Задача удалена");
            }else{
                sendNotFound(httpExchange, "Задача с таким ID не найдена");
            }
        } else {
            sendNotFound(httpExchange, "Такого пути нет");
        }
    }
}
