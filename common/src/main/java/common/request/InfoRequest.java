package common.request;

public class InfoRequest extends Request {

    private static final long serialVersionUID = 1L;

    public InfoRequest() {
        super("info");
    }

    @Override
    public Object getArguments(){
        return null;
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.NO_ARGS;
    }
}
