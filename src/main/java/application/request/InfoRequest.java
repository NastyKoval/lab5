package application.request;

public class InfoRequest extends Request {
    public InfoRequest() {
        super("info");
    }

    @Override
    public Object getArguments(){
        return null;
    }
}
