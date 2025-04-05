package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.HasInteractionsException;
import exceptions.NotFoundException;
import managers.TaskManager;
import model.Epic;
import model.Subtask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EpicHttpHandler extends BaseHttpHandler implements HttpHandler {
    public EpicHttpHandler(TaskManager taskManager, Gson gson) {
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

        Pattern epicPattern = Pattern.compile("^/epics/(\\d+)$");
        Pattern subtasksPattern = Pattern.compile("^/epics/(\\d+)/subtasks$");

        Matcher epicMatcher = epicPattern.matcher(path);
        Matcher epicSubtasksMatcher = subtasksPattern.matcher(path);

        if (path.matches("/epics")) {
            response = gson.toJson(taskManager.getEpics());
            sendText(httpExchange, response);
            System.out.println(response);
        } else if (epicMatcher.matches()) {
            int id = Integer.parseInt(epicMatcher.group(1));
            try {
                Epic epic = taskManager.getEpicById(id);
                response = gson.toJson(epic);
                sendText(httpExchange, response);
            } catch (NotFoundException e) {
                sendNotFound(httpExchange, e.getMessage());
            }
        } else if (epicSubtasksMatcher.matches()) {
            int id = Integer.parseInt(epicSubtasksMatcher.group(1));
            try {
                ArrayList<Subtask> subtasks = taskManager.getEpicById(id).getSubtasks();
                response = gson.toJson(subtasks);
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
        Epic epic = gson.fromJson(requestBody, Epic.class);

        if (epic == null) {
            sendMessage(httpExchange, "Тело запроса пустое", 400);
            return;
        }
        if (path.matches("/epics")) {
            try {
                taskManager.addNewEpic(epic);
                sendMessage(httpExchange, "Задача успешно добавлена", 201);
            } catch (HasInteractionsException e) {
                sendMessage(httpExchange, e.getMessage(), 406);
            }
        } else {
            sendNotFound(httpExchange, "Такого пути нет");
        }
    }

    private void handleDelete(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();

        Pattern epicPattern = Pattern.compile("^/epics/(\\d+)$");

        Matcher epicMatcher = epicPattern.matcher(path);

        if (path.matches("/epics")) {
            taskManager.deleteAllEpics();
            sendText(httpExchange, "Все задачи удалены");
        } else if (epicMatcher.matches()) {
            int id = Integer.parseInt(epicMatcher.group(1));
            if (taskManager.getEpicById(id) != null) {
                taskManager.deleteEpicById(id);
                sendText(httpExchange, "Задача удалена");
            } else {
                sendNotFound(httpExchange, "Задача с таким ID не найдена");
            }
        } else {
            sendNotFound(httpExchange, "Такого пути нет");
        }
    }
}