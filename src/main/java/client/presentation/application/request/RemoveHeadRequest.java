package client.presentation.application.request;
import client.presentation.application.command_cl.CommandTypeEnum;

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

    @Override
    public CommandTypeEnum getCommandType() {
        return CommandTypeEnum.NO_ARGS;
    }
}