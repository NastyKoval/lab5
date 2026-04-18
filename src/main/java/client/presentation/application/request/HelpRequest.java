package client.presentation.application.request;
import client.presentation.application.command_cl.CommandTypeEnum;

public class HelpRequest extends Request {

    public HelpRequest() {
        super("help");
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
