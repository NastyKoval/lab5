package client.presentation.application.request;
import common.request.CommandType;
import common.request.Request;

public class SaveRequest extends Request {

    public SaveRequest() {
        super("save");
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