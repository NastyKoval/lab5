package server.application.command;

import common.request.Request;
import common.response.Response;

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
        return new Response(true, "Программа завершена", null);
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