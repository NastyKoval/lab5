package common.request;

import common.domain.model.Flat;

public class AddRequest extends Request {

    private static final long serialVersionUID = 1L;
    private final Flat flat;

    public AddRequest(Flat flat) {
        super("add");
        this.flat = flat;
    }

    @Override
    public Object getArguments() {
        return flat;
    }

    public Flat getFlat() {
        return flat;
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.FLAT_ARG;
    }
}
