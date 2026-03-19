package application.request;

/**
 * Запрос на вывод поля furnish по убыванию
 * Команда: print_field_descending_furnish
 */
public class PrintFieldDescendingFurnishRequest extends Request {

    public PrintFieldDescendingFurnishRequest() {
        super("print_field_descending_furnish");
    }

    @Override
    public Object getArguments() {
        return null;
    }
}