package common.request;

public class HelpRequest extends Request {

    private static final long serialVersionUID = 1L;

    public HelpRequest() {
        super("help");
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
