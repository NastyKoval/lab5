package server.application.command;

import common.request.Request;
import common.response.Response;
import server.application.service.FlatService;
import common.domain.model.Flat;

import java.util.Optional;

public class RemoveHeadCommand implements CommandHandler {
    private final FlatService flatService;

    public RemoveHeadCommand(FlatService flatService) {
        this.flatService = flatService;
    }

    @Override
    public Response handle(Request request) {
        try {
            Optional<Flat> removed = flatService.removeHead();
            if (removed.isPresent()) {
                return new Response(true, "Первый элемент удалён: " + removed.get().getName(), null);
            } else {
                return new Response(false, "Коллекция пуста", null);
            }
        } catch (Exception e) {
            return new Response(false, "Ошибка: " + e.getMessage(), null);
        }
    }

    @Override
    public String getName() {
        return "remove_head";
    }

    @Override
    public String getDescription() {
        return "удалить первый элемент коллекции (head)";
    }
}