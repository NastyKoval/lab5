package server.application.command;

import common.domain.model.User;
import common.request.Request;
import common.response.Response;
import server.application.service.AuthService;

public class RegisterCommand implements CommandHandler {
    private final AuthService authService;

    public RegisterCommand(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public Response handle(Request request) {
        String[] args = request.getArguments();
        if (args == null || args.length < 2) {
            return new Response(false, "Укажите login и password", null);
        }

        try {
            User user = authService.register(args[0], args[1]);
            return new Response(true, "Регистрация успешна", user);
        } catch (IllegalArgumentException e) {
            return new Response(false, e.getMessage(), null);
        }
    }

    @Override public String getName() { return "register"; }
    @Override public String getDescription() { return "зарегистрировать нового пользователя"; }
}