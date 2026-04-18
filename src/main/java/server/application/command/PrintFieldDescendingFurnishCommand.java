package server.application.command;

import client.presentation.application.request.Request;
import server.application.response.Response;
import server.application.service.FlatService;
import domain.enums.Furnish;
import domain.model.Flat;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Команда вывода значений поля furnish в убывающем порядке.
 *
 * <p>Имя команды: "print_field_descending_furnish"</p>
 */
public class PrintFieldDescendingFurnishCommand implements CommandHandler {

    private final FlatService flatService;

    /**
     * Конструктор команды вывода поля furnish.
     *
     * @param flatService сервис для работы с квартирами
     */
    public PrintFieldDescendingFurnishCommand(FlatService flatService) {
        this.flatService = flatService;
    }

    /**
     * Выполняет команду вывода значений поля furnish в убывающем порядке.
     *
     * @param request запрос (не используется)
     * @return результат выполнения со списком значений furnish
     */
    @Override
    public Response handle(Request request) {
        try {
            // Получаем все квартиры
            List<Flat> flats = flatService.getAllFlats();

            Set<Furnish> furnishes = flats.stream()
                    .map(Flat::getFurnish)
                    .filter(f -> f != null)
                    .collect(Collectors.toCollection(() -> new TreeSet<Furnish>(Comparator.reverseOrder())));
            //TreeSet = особый вид коллекции( убирает повторы )
            //По умолчанию Furnish сортируется от а-я, а с Comparator.reverseOrder() обратный порядок

            // Проверяем результат
            if (furnishes.isEmpty()) {
                return new Response(true, "Нет данных о furnish", null);
            }

            // Преобразуем в строку для вывода
            StringBuilder output = new StringBuilder();
            for (Furnish furnish : furnishes) {
                output.append(furnish).append("\n");
            }

            // Возвращаем результат
            return new Response(true, output.toString(), null);

        } catch (Exception e) {
            return new Response(false, "Ошибка при выводе: " + e.getMessage(), null);
        }
    }

    /**
     * Получает имя команды.
     *
     * @return "print_field_descending_furnish"
     */
    @Override
    public String getName() {
        return "print_field_descending_furnish";
    }

    /**
     * Получает описание команды.
     *
     * @return описание что делает команда
     */
    @Override
    public String getDescription() {
        return "вывести значения поля furnish всех элементов в убывающем порядке";
    }
}