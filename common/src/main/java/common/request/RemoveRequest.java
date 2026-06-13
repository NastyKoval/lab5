package common.request;

import java.io.Serializable;

/**
 * Запрос на удаление квартиры по ID.
 */
public class RemoveRequest extends Request implements Serializable {

    private final int id;
    private static final long serialVersionUID = 1L;

    public RemoveRequest(int id) {
        super("remove_by_id");
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public Object getArguments() {
        return id;
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.ID_ARG;
    }
}