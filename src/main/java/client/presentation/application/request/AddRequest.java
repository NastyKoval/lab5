package client.presentation.application.request;
import client.presentation.application.command_cl.CommandTypeEnum;

import domain.model.Flat;

public class AddRequest extends Request {

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
    public CommandTypeEnum getCommandType() {
        return CommandTypeEnum.FLAT_ARG;
    }
}
