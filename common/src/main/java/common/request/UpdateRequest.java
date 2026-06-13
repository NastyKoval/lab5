package common.request;

import common.domain.model.Flat;

/**
 * Запрос на обновление квартиры по ID
 * Команда: update id {element}
 */
public class UpdateRequest extends Request {

    private static final long serialVersionUID = 1L;
    private final int id;
    private final Flat flat;

    public UpdateRequest(int id, Flat flat) {
        super("update");
        this.id = id;
        this.flat = flat;
    }

    public int getId() {
        return id;
    }

    public Flat getFlat() {
        return flat;
    }

    @Override
    public Object getArguments() {
        return flat;
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.ID_AND_FLAT;
    }
}