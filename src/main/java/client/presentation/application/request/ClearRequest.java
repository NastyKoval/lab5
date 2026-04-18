package client.presentation.application.request;
import client.presentation.application.command_cl.CommandTypeEnum;

public class ClearRequest extends Request {
    public ClearRequest() {
        super("clear");
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
