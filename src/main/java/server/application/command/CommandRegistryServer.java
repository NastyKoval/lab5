package server.application.command;

import server.application.response.Response;
import server.application.service.FlatService;
import server.application.service.ValidationResult;
import client.presentation.application.request.Request;

import java.util.HashMap;
import java.util.Map;

/**
 * Реестр команд на стороне сервера.
 * Отвечает за регистрацию и выполнение команд.
 */
public class CommandRegistryServer {
// Запусает команды
// Карта: имя команды -- обработчик
    private final Map<String, CommandHandler> handlers;
    private final FlatService flatService;

    /**
     * Конструктор.
     * @param flatService сервис для работы с квартирами
     */
    public CommandRegistryServer(FlatService flatService) {
        this.handlers = new HashMap<>();
        this.flatService = flatService;
        registerAllHandlers();
    }

    /**
     * Регистрирует все обработчики команд.
     */
    private void registerAllHandlers() {
        register(new AddCommand(flatService));
        register(new RemoveCommand(flatService));
        register(new UpdateCommand(flatService));
        register(new ShowCommand(flatService));
        register(new HelpCommand());
        register(new InfoCommand(flatService));
        register(new ClearCommand(flatService));
        register(new SaveCommand(flatService));
        register(new ExitCommand());
        register(new RemoveFirstCommand(flatService));
        register(new RemoveHeadCommand(flatService));
        register(new AddIfMinCommand(flatService));
        register(new CountLessThanBathroomsCommand(flatService));
        register(new FilterGreaterThanHouseCommand(flatService));
        register(new PrintFieldDescendingFurnishCommand(flatService));
        // register(new ExecuteScriptCommand(flatService)); // если есть
    }

    /**
     * Регистрирует один обработчик.
     */
    public void register(CommandHandler handler) {
        handlers.put(handler.getName(), handler);
    }

    /**
     * Находит обработчик по запросу.
     */
    private CommandHandler getHandler(Request request) {
        String commandName = request.getCommandName();
        CommandHandler handler = handlers.get(commandName);

        if (handler == null) {
            throw new RuntimeException("Команда не найдена: " + commandName);
        }

        return handler;
    }

    /**
     * Выполняет команду.
     * @param request запрос на выполнение
     * @return результат выполнения
     */
    public Response execute(Request request) {
        try {
            // Валидация аргументов
            ValidationResult validation = flatService.validateArguments(request);
            if (!validation.isValid()) {
                return new Response(false, validation.getErrorMessage(), null);
            }

            //Поиск обработчика
            CommandHandler handler = getHandler(request);

            // Выполнение команды ! ! ! !
            return handler.handle(request);

        } catch (Exception e) {
            return new Response(false, "Ошибка: " + e.getMessage(), null);
        }
    }
}