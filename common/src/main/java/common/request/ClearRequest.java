package common.request;

public class ClearRequest extends Request {

    private static final long serialVersionUID = 1L;

    public ClearRequest() {
        super("clear");
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
