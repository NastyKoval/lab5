package common.domain.model;

import common.util.Validator;
import java.io.Serializable;
import java.util.Objects;


public class House implements Comparable<House>, Serializable{

    private String name;
    private Long year;
    private Long numberOfFloors;
    private Integer numberOfFlatsOnFloor;
    private Long numberOfLifts;
    private static final long serialVersionUID = 1L;
    public House(String name, Long year, Long numberOfFloors,
                 Integer numberOfFlatsOnFloor, Long numberOfLifts) {
        this.name = name;
        this.year = year;
        this.numberOfFloors = numberOfFloors;
        this.numberOfFlatsOnFloor = numberOfFlatsOnFloor;
        this.numberOfLifts = numberOfLifts;
        validate();
    }

    private void validate() {
        Validator.validateNotNull(name, "Название дома");
        Validator.validateYear(year);
        Validator.validateFloors(numberOfFloors);
        Validator.validatePositive(numberOfFlatsOnFloor, "Количество квартир на этаже");
        Validator.validatePositive(numberOfLifts, "Количество лифтов");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        Validator.validateString(name, "Название дома");
        this.name = name;
    }

    public Long getYear() {
        return year;
    }

    public void setYear(Long year) {
        Validator.validateYear(year);
        this.year = year;
    }

    public Long getNumberOfFloors() {
        return numberOfFloors;
    }

    public void setNumberOfFloors(Long numberOfFloors) {
        Validator.validateFloors(numberOfFloors);
        this.numberOfFloors = numberOfFloors;
    }

    public Integer getNumberOfFlatsOnFloor() {
        return numberOfFlatsOnFloor;
    }

    public void setNumberOfFlatsOnFloor(Integer numberOfFlatsOnFloor) {
        Validator.validatePositive(numberOfFlatsOnFloor, "Количество квартир на этаже");
        this.numberOfFlatsOnFloor = numberOfFlatsOnFloor;

    }

    public Long getNumberOfLifts() {
        return numberOfLifts;
    }

    public void setNumberOfLifts(Long numberOfLifts) {
        Validator.validatePositive(numberOfLifts, "Количество лифтов");
        this.numberOfLifts = numberOfLifts;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        House house = (House) o;
        return Objects.equals(name, house.name) && Objects.equals(year, house.year) && Objects.equals(numberOfFloors, house.numberOfFloors) && Objects.equals(numberOfFlatsOnFloor, house.numberOfFlatsOnFloor) && Objects.equals(numberOfLifts, house.numberOfLifts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, year, numberOfFloors, numberOfFlatsOnFloor, numberOfLifts);
    }

    @Override
    public String toString() {
        return "House{" +
                "name='" + name + '\'' +
                ", year=" + year +
                ", floors=" + numberOfFloors +
                ", lifts=" + numberOfLifts +
                '}';
    }

    @Override
    public int compareTo(House other) {
        if (other == null) return 1;

        if (this.year != null && other.year != null) {
            int yearCompare = Long.compare(this.year, other.year);
            if (yearCompare != 0) return yearCompare;
        }

        if (this.numberOfFloors != null && other.numberOfFloors != null) {
            int floorsCompare = Long.compare(this.numberOfFloors, other.numberOfFloors);
            if (floorsCompare != 0) return floorsCompare;
        }

        if (this.name != null && other.name != null) {
            return this.name.compareTo(other.name);
        }

        return 0;
    }
}
