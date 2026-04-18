package server.application.command;

import client.presentation.application.request.Request;
import server.application.response.Response;

/**
 * Команда завершения программы.
 *
 * <p>Имя команды: "exit"</p>
 *
 */
public class ExitCommand implements CommandHandler {

    /**
     * Выполняет команду завершения программы.
     *
     * @param request запрос (не используется)
     * @return null (программа завершается)
     */
    @Override
    public Response handle(Request request) {
        System.exit(0);
        return null; // Никогда не выполняется
    }

    /**
     * Получает имя команды.
     *
     * @return "exit"
     */
    @Override
    public String getName() {
        return "exit";
    }

    /**
     * Получает описание команды.
     *
     * @return описание что делает команда
     */
    @Override
    public String getDescription() {
        return "завершить программу";
    }
}