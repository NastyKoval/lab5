package application.request;

public class ClearRequest extends Request {
    public ClearRequest() {
        super("clear");
    }

    @Override
    public Object getArguments() {
        return null;
    }
}
