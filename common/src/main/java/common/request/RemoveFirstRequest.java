package common.request;

/**
 * Запрос на удаление первого элемента
 * Команда: remove_first
 */
public class RemoveFirstRequest extends Request {

    private static final long serialVersionUID = 1L;

    public RemoveFirstRequest() {
        super("remove_first");
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