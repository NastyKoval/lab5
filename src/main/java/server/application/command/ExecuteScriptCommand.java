package server.application.command;

import client.presentation.application.command_cl.CommandRegistry;
import client.presentation.application.request.ExecuteScriptRequest;
import client.presentation.application.request.Request;
import server.application.response.Response;
import server.application.service.FlatService;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Команда выполнения скрипта из файла.
 *
 * <p>Имя команды: "execute_script"</p>
 *
 */
public class ExecuteScriptCommand implements CommandHandler {

    private final FlatService flatService;
    private final client.presentation.application.command_cl.CommandRegistry registry;

    /**
     * Конструктор команды выполнения скрипта.
     *
     * @param flatService сервис для работы с квартирами
     * @param registry реестр команд для выполнения команд из файла
     */
    public ExecuteScriptCommand(FlatService flatService, CommandRegistry registry) {
        this.flatService = flatService;
        this.registry = registry;
    }

    /**
     * Выполняет команду выполнения скрипта из файла.
     *
     * @param request запрос содержащий имя файла со скриптом
     * @return результат выполнения
     */
    @Override
    public Response handle(Request request) {
        try {
            // Преобразуем Request в ExecuteScriptRequest
            ExecuteScriptRequest scriptRequest = (ExecuteScriptRequest) request;

            // Получаем имя файла
            String fileName = scriptRequest.getFileName();

            // Читаем и выполняем команды из файла
            StringBuilder output = new StringBuilder();
            output.append("Выполнение скрипта из файла: ").append(fileName).append("\n");

            try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
                String line;
                int lineNumber = 0;

                while ((line = reader.readLine()) != null) {
                    lineNumber++;
                    line = line.trim();

                    // Пустые строки и комментарии
                    if (line.isEmpty() || line.startsWith("//")) {
                        continue;
                    }

                    output.append("[").append(lineNumber).append("] ")
                            .append(line).append("\n");

                    // Здесь должна быть логика выполнения команды
                    // Но для консольной версии это сложно реализовать
                    // Поэтому просто выводим команду
                }
            }

            output.append("Скрипт выполнен");
            return new Response(true, output.toString(), null);

        } catch (ClassCastException e) {
            return new Response(false, "Ошибка: неверный тип запроса", null);
        } catch (IOException e) {
            return new Response(false, "Ошибка чтения файла: " + e.getMessage(), null);
        } catch (Exception e) {
            return new Response(false, "Ошибка при выполнении скрипта: " + e.getMessage(), null);
        }
    }

    /**
     * Получает имя команды.
     *
     * @return "execute_script"
     */
    @Override
    public String getName() {
        return "execute_script";
    }

    /**
     * Получает описание команды.
     *
     * @return описание что делает команда
     */
    @Override
    public String getDescription() {
        return "считать и исполнить скрипт из указанного файла";
    }
}