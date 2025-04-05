package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;

import java.io.IOException;

public class HistoryHttpHandler extends BaseHttpHandler implements HttpHandler {
    public HistoryHttpHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        if (httpExchange.getRequestMethod().equals("GET")) {
            sendText(httpExchange, gson.toJson(taskManager.getHistory()));
        } else {
            sendNotFound(httpExchange, "Такого метода нет");
        }
    }
}
