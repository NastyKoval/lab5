package common.domain.model;

import common.domain.enums.Furnish;
import common.domain.enums.View;
import common.util.Validator;
import java.io.Serializable;

import java.time.LocalDateTime;
import java.util.Objects;

public class Flat implements Comparable<Flat>, Serializable {
    private int id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private java.time.LocalDateTime creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private Double area; //Значение поля должно быть больше 0
    private Integer numberOfRooms; //Поле может быть null, Значение поля должно быть больше 0
    private Long numberOfBathrooms; //Значение поля должно быть больше 0
    private Furnish furnish; //Поле может быть null
    private View view; //Поле не может быть null
    private House house; //Поле может быть null
    private static final long serialVersionUID = 1L;
    public Flat(String name, Coordinates coordinates, Double area, Integer numberOfRooms, Long numberOfBathrooms, Furnish furnish, View view, House house) {
        this.name = name;
        this.coordinates = coordinates;
        this.area = area;
        this.numberOfRooms = numberOfRooms;
        this.numberOfBathrooms = numberOfBathrooms;
        this.furnish = furnish;
        this.view = view;
        this.house = house;
        validate();
    }
    private void validate(){
        Validator.validateString(name, "Название квартиры");
        Validator.validateNotNull(coordinates, "Координаты");
        Validator.validatePositive(area, "Площадь");
        Validator.validatePositive(numberOfRooms, "Количество комнат");
        Validator.validatePositive(numberOfBathrooms, "Количество ванных комнат");
        Validator.validateNotNull(view, "Вид из окна");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        Validator.validateId(id, "id");
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        Validator.validateString(name, "Название квартиры");
        this.name = name;
    }

    public Coordinates getCoordinates(){
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates){
        Validator.validateNotNull(coordinates, "Координаты");
        this.coordinates = coordinates;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime date) {
        Validator.validateNotNull(date, "Дата создания");
        this.creationDate = date;
    }

    public Double getArea() {
        return area;
    }

    public void setArea(Double area) {
        Validator.validatePositive(area, "Площадь");
        this.area = area;
    }

    public Integer getNumberOfRooms() {
        return numberOfRooms;
    }

    public void setNumberOfRooms(Integer numberOfRooms) {
        Validator.validatePositive(numberOfRooms, "Количество комнат");
        this.numberOfRooms = numberOfRooms;
    }

    public Long getNumberOfBathrooms() {
        return numberOfBathrooms;
    }

    public void setNumberOfBathrooms(Long numberOfBathrooms) {
        Validator.validatePositive(numberOfBathrooms, "Количество ванных комнат");
        this.numberOfBathrooms = numberOfBathrooms;
    }

    public Furnish getFurnish() {
        return furnish;
    }

    public void setFurnish(Furnish furnish) {
        this.furnish = furnish;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        Validator.validateNotNull(view, "Вид из окна");
        this.view = view;
    }

    public House getHouse() {
        return house;
    }

    public void setHouse(House house) {
        this.house = house;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Flat flat = (Flat) o;
        return id == flat.id && numberOfBathrooms == flat.numberOfBathrooms && Objects.equals(name, flat.name) && Objects.equals(coordinates, flat.coordinates) && Objects.equals(creationDate, flat.creationDate) && Objects.equals(area, flat.area) && Objects.equals(numberOfRooms, flat.numberOfRooms) && furnish == flat.furnish && view == flat.view && Objects.equals(house, flat.house);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, coordinates, creationDate, area, numberOfRooms, numberOfBathrooms, furnish, view, house);
    }

    @Override
    public String toString() {
        return "Flat{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coordinates=" + coordinates +
                ", creationDate=" + creationDate +
                ", area=" + area +
                ", numberOfRooms=" + numberOfRooms +
                ", numberOfBathrooms=" + numberOfBathrooms +
                ", furnish=" + furnish +
                ", view=" + view +
                ", house=" + house +
                '}';
    }

    @Override
    public int compareTo(Flat other) {
        if (other == null) {
            return 1;
        }

        // Сравниваем по площади
        // Обрабатываем случай когда area может быть null
        if (this.area == null && other.area == null) {
            // Обе null — переходим к сравнению по названию
        } else if (this.area == null) {
            return -1;  // null считаем меньше любого числа
        } else if (other.area == null) {
            return 1;   // любое число больше null
        } else {
            // Обе не null — сравниваем значения
            int areaCompare = Double.compare(this.area, other.area);
            if (areaCompare != 0) {
                return areaCompare;
            }
        }

        // если площади равны — сравниваем по названию
        if (this.name != null && other.name != null) {
            return this.name.compareTo(other.name);
        }

        return 0;
    }
}
