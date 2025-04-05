package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import exceptions.HasInteractionsException;
import exceptions.NotFoundException;
import managers.TaskManager;
import model.Subtask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SubtaskHttpHandler extends BaseHttpHandler {
    public SubtaskHttpHandler(TaskManager taskManager, Gson gson) {
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

    public void handleGet(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        String response;

        Pattern subtaskPattern = Pattern.compile("^/subtasks/(\\d+)$");
        Matcher subtaskMatcher = subtaskPattern.matcher(path);

        if (path.matches("/subtasks")) {
            response = gson.toJson(taskManager.getSubtasks());
            sendText(httpExchange, response);
            System.out.println(response);
        } else if (subtaskMatcher.matches()) {
            int id = Integer.parseInt(subtaskMatcher.group(1));
            try {
                Subtask subtask = taskManager.getSubtaskById(id);
                response = gson.toJson(subtask);
                sendText(httpExchange, response);
            } catch (NotFoundException e) {
                sendNotFound(httpExchange, e.getMessage());
            }
        } else {
            sendNotFound(httpExchange, "Такого пути нет");
        }
    }

    public void handlePost(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();

        String requestBody = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Subtask subtask = gson.fromJson(requestBody, Subtask.class);

        if (subtask == null) {
            sendMessage(httpExchange, "Тело запроса пустое", 400);
            return;
        }

        if (path.matches("/subtasks")) {
            int id = subtask.getId();

            if (id != 0) {
                taskManager.updateSubtask(subtask);
                sendMessage(httpExchange, "Задача успешно обновлена", 201);
            } else {
                try {
                    taskManager.addNewSubtask(subtask);
                    sendMessage(httpExchange, "Задача успешно добавлена", 201);
                } catch (NotFoundException | HasInteractionsException e) {
                    sendNotFound(httpExchange, e.getMessage());
                }
            }
        } else {
            sendNotFound(httpExchange, "Такого пути нет");
        }
    }

    private void handleDelete(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();

        Pattern subtaskPattern = Pattern.compile("^/subtasks/(\\d+)$");
        Matcher subtaskMatcher = subtaskPattern.matcher(path);

        if (path.matches("/subtasks")) {
            taskManager.deleteAllSubtasks();
            sendText(httpExchange, "Все задачи удалены");
        } else if (subtaskMatcher.matches()) {
            int id = Integer.parseInt(subtaskMatcher.group(1));
            try {
                taskManager.deleteSubtaskById(id);
                sendText(httpExchange, "Задача удалена");
            } catch (NotFoundException e) {
                sendNotFound(httpExchange, e.getMessage());
            }
        } else {
            sendNotFound(httpExchange, "Такого пути нет");
        }
    }
}
