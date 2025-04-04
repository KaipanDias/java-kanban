package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;
import model.Subtask;
import model.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SubtaskHttpHandler  extends BaseHttpHandler implements HttpHandler {
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

    public void handleGet(HttpExchange httpExchange) throws IOException{
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
            if (taskManager.getTaskById(id) != null){
                response = gson.toJson(taskManager.getSubtaskById(id));
                sendText(httpExchange, response);
            }else{
                sendNotFound(httpExchange, "Подзадача с таким ID не найдена");
            }
        } else {
            sendNotFound(httpExchange, "Такого пути нет");
        }
    }

    public void handlePost(HttpExchange httpExchange) throws IOException{
        String path = httpExchange.getRequestURI().getPath();

        String requestBody = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Subtask subtask = gson.fromJson(requestBody, Subtask.class);

        if (subtask == null){
            sendMessage(httpExchange, "Тело запроса пустое", 400);
            return;
        }

        if (path.matches("/subtasks")) {
            int id = subtask.getId();

            if (id != 0){
                taskManager.updateTask(subtask);
                sendMessage(httpExchange, "Задача успешно обновлена", 201);
            }else{
                int newTaskId = taskManager.addNewSubtask(subtask);

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

        Pattern subtaskPattern = Pattern.compile("^/tasks/(\\d+)$");
        Matcher subtaskMatcher = subtaskPattern.matcher(path);

        if (path.matches("/tasks")) {
            taskManager.deleteAllSubtasks();
            sendText(httpExchange, "Все задачи удалены");
        } else if (subtaskMatcher.matches()) {
            int id = Integer.parseInt(subtaskMatcher.group(1));
            if (taskManager.getTaskById(id) != null){
                taskManager.deleteSubtaskById(id);
                sendText(httpExchange, "Задача удалена");
            }else{
                sendNotFound(httpExchange, "Задача с таким ID не найдена");
            }
        } else {
            sendNotFound(httpExchange, "Такого пути нет");
        }
    }
}
