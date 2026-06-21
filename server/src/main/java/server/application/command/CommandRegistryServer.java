package server.application.command;

import common.request.Request;
import common.response.Response;
import server.application.context.UserContext;
import server.application.service.AuthService;
import server.application.service.FlatService;
import server.application.service.ValidationResult;

import java.util.HashMap;
import java.util.Map;

/**
 * Реестр команд на стороне сервера.
 * Отвечает за регистрацию обработчиков(класс команды) и выполнение команд.
 */
public class CommandRegistryServer {

    private final Map<String, CommandHandler> handlers;
    private final FlatService flatService;
    private final AuthService authService;

    /**
     * Конструктор реестра.
     * @param flatService сервис для работы с квартирами
     * @param authService сервис для работы с пользователями
     */
    public CommandRegistryServer(FlatService flatService, AuthService authService) {
        this.handlers = new HashMap<>();
        this.flatService = flatService;
        this.authService = authService;
        registerAllHandlers();
    }

    /**
     * Регистрирует все доступные команды.
     */
    private void registerAllHandlers() {
        // Команды работы с коллекцией квартир
        register(new AddCommand(flatService));
        register(new RemoveCommand(flatService));
        register(new UpdateCommand(flatService));
        register(new ShowCommand(flatService));
        register(new HelpCommand(flatService));
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
        register(new ExecuteScriptCommand(flatService));

        // Команды авторизации и регистрации пользователей
        register(new LoginCommand(authService));
        register(new RegisterCommand(authService));
    }

    /**
     * Регистрирует один обработчик команды.
     */
    public void register(CommandHandler handler) {
        handlers.put(handler.getName(), handler);
    }

    /**
     * Находит обработчик по типу команды из запроса.
     */
    private CommandHandler getHandler(Request request) {
        String commandName = request.getType().name().toLowerCase();
        CommandHandler handler = handlers.get(commandName);

        if (handler == null) {
            throw new RuntimeException("Команда не найдена: " + commandName);
        }

        return handler;
    }

    /**
     * Выполняет команду.
     * Проверяет авторизацию, валидирует аргументы и вызывает обработчик.
     */
    public Response execute(Request request) {
        try {
            // Логирование входящего запроса для отладки
            System.out.println("[SERVER] Получен запрос: type=" + request.getType() +
                    ", isPublic=" + request.getType().isPublic() +
                    ", user=" + request.getUser());

            // Проверка авторизации для команд, требующих входа в систему
            if (!request.getType().isPublic()) {
                if (request.getUser() == null) {
                    System.out.println("[SERVER] Отказано в доступе: требуется авторизация");
                    return new Response(false, "Требуется авторизация. Выполните: login <login> <password>", null);
                }
                // Устанавливаем пользователя в контекст текущего потока
                UserContext.setUser(request.getUser());
                System.out.println("[SERVER] Пользователь авторизован: " + request.getUser().getLogin());
            }

            // Валидация аргументов запроса
            ValidationResult validation = flatService.validateArguments(request);
            if (!validation.isValid()) {
                System.out.println("[SERVER] Ошибка валидации: " + validation.getErrorMessage());
                return new Response(false, validation.getErrorMessage(), null);
            }

            // Поиск и выполнение обработчика команды
            CommandHandler handler = getHandler(request);
            System.out.println("[SERVER] Выполнение команды: " + request.getType());
            Response result = handler.handle(request);
            System.out.println("[SERVER] Команда выполнена, ответ: success=" + result.isSuccess());
            return result;

        } catch (Exception e) {
            System.err.println("[SERVER] Исключение при выполнении команды: " + e.getMessage());
            e.printStackTrace();
            return new Response(false, "Ошибка: " + e.getMessage(), null);
        } finally {
            // Очищаем контекст пользователя после выполнения запроса
            UserContext.clear();
        }
    }
}