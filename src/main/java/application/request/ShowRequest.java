package application.request;

public class ShowRequest extends Request {
    public ShowRequest() {
        super("show");
    }

    @Override
    public Object getArguments() {
        return null;
    }
}