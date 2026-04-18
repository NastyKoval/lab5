package client.presentation.application.request;
import client.presentation.application.command_cl.CommandTypeEnum;

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

    @Override
    public CommandTypeEnum getCommandType() {
        return CommandTypeEnum.NO_ARGS;
    }
}