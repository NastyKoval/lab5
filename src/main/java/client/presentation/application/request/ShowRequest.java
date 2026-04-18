package client.presentation.application.request;
import client.presentation.application.command_cl.CommandTypeEnum;

public class ShowRequest extends Request {
    public ShowRequest() {
        super("show");
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