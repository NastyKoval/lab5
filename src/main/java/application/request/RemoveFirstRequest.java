package application.request;

/**
 * Запрос на удаление первого элемента
 * Команда: remove_first
 */
public class RemoveFirstRequest extends Request {

    public RemoveFirstRequest() {
        super("remove_first");
    }

    @Override
    public Object getArguments() {
        return null;
    }
}