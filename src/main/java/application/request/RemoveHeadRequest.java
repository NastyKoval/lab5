package application.request;

/**
 * Запрос на вывод и удаление первого элемента
 * Команда: remove_head
 */
public class RemoveHeadRequest extends Request {

    public RemoveHeadRequest() {
        super("remove_head");
    }

    @Override
    public Object getArguments() {
        return null;
    }
}