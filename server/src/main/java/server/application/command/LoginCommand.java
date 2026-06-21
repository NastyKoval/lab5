package server.application.command;

import common.domain.model.User;
import common.request.Request;
import common.response.Response;
import server.application.service.AuthService;

public class LoginCommand implements CommandHandler {
    private final AuthService authService;

    public LoginCommand(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public Response handle(Request request) {
        String[] args = request.getArguments();
        if (args == null || args.length < 2) {
            return new Response(false, "Укажите login и password", null);
        }

        User user = authService.login(args[0], args[1]);
        if (user != null) {
            return new Response(true, "Авторизация успешна", user); // ✅ Клиент сохранит этот объект
        } else {
            return new Response(false, "Неверный логин или пароль", null);
        }
    }

    @Override public String getName() { return "login"; }
    @Override public String getDescription() { return "войти в систему"; }
}