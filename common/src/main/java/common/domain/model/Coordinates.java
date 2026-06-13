package common.domain.model;

import common.util.Validator;

import java.io.Serializable;
import java.util.Objects;

public class Coordinates implements Serializable {
    private Integer x; //Поле не может быть null
    private float y;
    private static final long serialVersionUID = 1L;
    public Coordinates(Integer x, float y){
        Validator.validateNotNull(x, "X координата");
        this.x = x;
        this.y = y;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        Validator.validateNotNull(x, "X координата");
        this.x = x;
    }

    public float getY() {
        return y;
    }
    public void setY(float y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "Coordinates{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates that = (Coordinates) o;
        return Float.compare(y, that.y) == 0 && Objects.equals(x, that.x);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
