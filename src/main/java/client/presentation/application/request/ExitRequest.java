package client.presentation.application.request;
import client.presentation.application.command_cl.CommandTypeEnum;

public class ExitRequest extends Request {
    public ExitRequest() {
        super("exit");
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
