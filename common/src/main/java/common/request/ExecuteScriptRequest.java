package common.request;

/**
 * Запрос на выполнение скрипта из файла
 * Команда: execute_script file_name
 */
public class ExecuteScriptRequest extends Request {

    private static final long serialVersionUID = 1L;
    private final String fileName;

    public ExecuteScriptRequest(String fileName) {
        super("execute_script");
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public Object getArguments() {
        return fileName;
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.STRING_ARG;
    }
}