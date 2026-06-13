package common.request;

import common.domain.model.Flat;

/**
 * Запрос на добавление если значение меньше минимального
 * Команда: add_if_min {element}
 */
public class AddIfMinRequest extends Request {

    private static final long serialVersionUID = 1L;
    private final Flat flat;

    public AddIfMinRequest(Flat flat) {
        super("add_if_min");
        this.flat = flat;
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
        return CommandType.FLAT_ARG;
    }
}