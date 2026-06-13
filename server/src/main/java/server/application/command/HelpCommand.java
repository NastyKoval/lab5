package server.application.command;

import common.request.Request;
import common.response.Response;

/**
 * Команда help — вывод справки по командам.
 */
public class HelpCommand implements CommandHandler {

    /**
     * Конструктор без параметров.
     */
    public HelpCommand() {

    }

    @Override
    public Response handle(Request request) {
        String helpText = """
            Доступные команды:
              help                              - вывести справку
              info                              - информация о коллекции
              show                              - показать все элементы
              add {element}                     - добавить элемент
              update id {element}               - обновить элемент
              remove_by_id id                   - удалить по ID
              clear                             - очистить коллекцию
              save                              - сохранить в файл
              exit                              - завершить программу
              remove_first                      - удалить первый элемент
              remove_head                       - вывести и удалить первый
              add_if_min {element}              - добавить если меньше минимума
              count_less_than_number_of_bathrooms value - подсчитать количество
              filter_greater_than_house {element} - фильтровать по house
              print_field_descending_furnish    - вывести поле furnish
            """;

        return new Response(true, helpText, null);
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "вывести справку по доступным командам";
    }
}