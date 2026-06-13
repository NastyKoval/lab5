package common.request;

/**
 * Запрос на вывод поля furnish по убыванию
 * Команда: print_field_descending_furnish
 */
public class PrintFieldDescendingFurnishRequest extends Request {

    private static final long serialVersionUID = 1L;

    public PrintFieldDescendingFurnishRequest() {
        super("print_field_descending_furnish");
    }

    @Override
    public Object getArguments() {
        return null;
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.NO_ARGS;
    }
}