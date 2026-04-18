package client.presentation.application.request;
import client.presentation.application.command_cl.CommandTypeEnum;

public class RemoveRequest extends Request {

    private final int id;

    public RemoveRequest(int id) {
        super("remove");
        this.id = id;
    }

    @Override
    public Object getArguments() {
        return id;
    }

    public int getId() {
        return id;
    }

    @Override
    public CommandTypeEnum getCommandType() {
        return CommandTypeEnum.ID_ARG;
    }
}

