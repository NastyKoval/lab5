package application.request;

import domain.model.Flat;

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
}
