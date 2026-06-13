package common.request;

/**
 * Запрос на вывод и удаление первого элемента
 * Команда: remove_head
 */
public class RemoveHeadRequest extends Request {

    private static final long serialVersionUID = 1L;

    public RemoveHeadRequest() {
        super("remove_head");
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