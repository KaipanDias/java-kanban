package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;

import java.io.IOException;

public class PrioritizedHttpHandler  extends BaseHttpHandler implements HttpHandler {
    public PrioritizedHttpHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        if (httpExchange.getRequestMethod().equals("GET")){
            sendText(httpExchange, gson.toJson(taskManager.getPrioritizedTasks()));
        }else{
            sendNotFound(httpExchange, "Такого метода нет");
        }
    }
}
