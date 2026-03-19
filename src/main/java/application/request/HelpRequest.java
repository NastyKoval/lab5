package application.request;

public class HelpRequest extends Request {

    public HelpRequest() {
        super("help");
    }

    @Override
    public Object getArguments() {
        return null;
    }
}
