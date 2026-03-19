package application.request;

import domain.model.Flat;
/**
 * Запрос на обновление квартиры по ID
 * Команда: update id {element}
 */
public class UpdateRequest extends Request {

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
}