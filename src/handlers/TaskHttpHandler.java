package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.HasInteractionsException;
import exceptions.NotFoundException;
import managers.TaskManager;
import model.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TaskHttpHandler extends BaseHttpHandler implements HttpHandler {
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
            try {
                Task task = taskManager.getTaskById(id);
                response = gson.toJson(task);
                sendText(httpExchange, response);
            } catch (NotFoundException e) {
                sendNotFound(httpExchange, e.getMessage());
            }
        } else {
            sendNotFound(httpExchange, "Такого пути нет");
        }
    }

    private void handlePost(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();

        String requestBody = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Task task = gson.fromJson(requestBody, Task.class);

        if (task == null) {
            sendMessage(httpExchange, "Тело запроса пустое", 400);
            return;
        }
        if (path.matches("/tasks")) {
            int id = task.getId();
            if (id != 0) {
                try {
                    taskManager.updateTask(task);
                    sendMessage(httpExchange, "Задача успешно обновлена", 201);
                } catch (HasInteractionsException e) {
                    sendMessage(httpExchange, e.getMessage(), 406);
                }
            } else {
                try {
                    taskManager.addNewTask(task);
                    sendMessage(httpExchange, "Задача успешно добавлена", 201);
                } catch (HasInteractionsException e) {
                    sendMessage(httpExchange, e.getMessage(), 406);
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
            try {
                taskManager.deleteTaskById(id);
                sendText(httpExchange, "Задача удалена");
            } catch (NotFoundException e) {
                sendNotFound(httpExchange, e.getMessage());
            }
        } else {
            sendNotFound(httpExchange, "Такого пути нет");
        }
    }
}
