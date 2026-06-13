package common.request;

public class ShowRequest extends Request {

    private static final long serialVersionUID = 1L;

    public ShowRequest() {
        super("show");
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