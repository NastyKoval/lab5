package client.presentation.application.request;
import common.request.CommandType;
import common.request.Request;

public class ExitRequest extends Request {
    public ExitRequest() {
        super("exit");
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
