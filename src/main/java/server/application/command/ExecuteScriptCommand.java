package server.application.command;

import client.presentation.application.request.Request;
import server.application.response.Response;

public class ExecuteScriptCommand implements CommandHandler {

    @Override
    public Response handle(Request request) {
        return new Response(
                false,
                "Команда execute_script выполняется на клиенте",
                null
        );
    }

    @Override
    public String getName() {
        return "execute_script";
    }

    @Override
    public String getDescription() {
        return "Выполнить скрипт из файла ";
    }
}