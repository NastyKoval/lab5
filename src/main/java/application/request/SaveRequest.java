package application.request;

public class SaveRequest extends Request {

    public SaveRequest() {
        super("save");
    }

    @Override
    public Object getArguments() {
        return null;
    }
}