package application.request;

public class ExitRequest extends Request {
    public ExitRequest() {
        super("exit");
    }

    @Override
    public Object getArguments() {
        return null;
    }
}
