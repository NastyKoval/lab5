package application.response;

public class Response {

    private final boolean success;
    private final String output;

    public Response(boolean success, String output) {
        this.success = success;
        this.output = output;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getOutput() {
        return output;
    }
}