package client.presentation.application.request;
import client.presentation.application.command_cl.CommandTypeEnum;

public class InfoRequest extends Request {
    public InfoRequest() {
        super("info");
    }

    @Override
    public Object getArguments(){
        return null;
    }

    @Override
    public CommandTypeEnum getCommandType() {
        return CommandTypeEnum.NO_ARGS;
    }
}
