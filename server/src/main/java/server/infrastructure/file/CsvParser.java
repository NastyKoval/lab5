package server.infrastructure.file;

import common.domain.enums.Furnish;
import common.domain.enums.View;
import common.domain.model.Coordinates;
import common.domain.model.Flat;
import common.domain.model.House;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс для парсинга объектов Flat в CSV и обратно.
 */
public class CsvParser {

    private static final String DELIMITER = ",";

    /**
     * Преобразовать объект Flat в CSV строку.
     */
    public String toCsv(Flat flat) {
        StringBuilder sb = new StringBuilder();

        sb.append(flat.getId()).append(DELIMITER);
        sb.append(flat.getName()).append(DELIMITER);
        sb.append(flat.getCoordinates().getX()).append(DELIMITER);
        sb.append(flat.getCoordinates().getY()).append(DELIMITER);
        sb.append(flat.getCreationDate()).append(DELIMITER);
        sb.append(flat.getArea()).append(DELIMITER);

        sb.append(flat.getNumberOfRooms() != null ? flat.getNumberOfRooms() : "").append(DELIMITER);
        sb.append(flat.getNumberOfBathrooms() != null ? flat.getNumberOfBathrooms() : "").append(DELIMITER);

        sb.append(flat.getFurnish() != null ? flat.getFurnish().name() : "").append(DELIMITER);
        sb.append(flat.getView() != null ? flat.getView().name() : "").append(DELIMITER);

        House house = flat.getHouse();
        if (house != null) {
            sb.append(house.getName() != null ? house.getName() : "").append(DELIMITER);
            sb.append(house.getYear() != null ? house.getYear() : "").append(DELIMITER);
            sb.append(house.getNumberOfFloors() != null ? house.getNumberOfFloors() : "").append(DELIMITER);
            sb.append(house.getNumberOfFlatsOnFloor() != null ? house.getNumberOfFlatsOnFloor() : "").append(DELIMITER);
            sb.append(house.getNumberOfLifts() != null ? house.getNumberOfLifts() : "");
        } else {
            sb.append(",,,");
        }

        return sb.toString();
    }

    /**
     * Преобразовать CSV строку в объект Flat.
     */
    public Flat fromCsv(String line) {
        String[] parts = line.split(DELIMITER, -1);

        int id = Integer.parseInt(parts[0]);
        String name = parts[1];

        Integer x = Integer.parseInt(parts[2]);
        float y = Float.parseFloat(parts[3]);
        Coordinates coordinates = new Coordinates(x, y);

        // Безопасный парсинг creationDate( даже если в дате ошибка)
        LocalDateTime creationDate;
        if (parts.length > 4 && parts[4] != null && !parts[4].trim().isEmpty() && !parts[4].equals("null")) {
            try {
                creationDate = LocalDateTime.parse(parts[4]);
            } catch (DateTimeParseException e) {
                creationDate = LocalDateTime.now();
            }
        } else {
            creationDate = LocalDateTime.now();
        }

        Double area = Double.parseDouble(parts[5]);
        Integer numberOfRooms = (parts.length > 6 && !parts[6].isEmpty()) ? Integer.parseInt(parts[6]) : null;
        Long numberOfBathrooms = (parts.length > 7 && !parts[7].isEmpty()) ? Long.parseLong(parts[7]) : null;
        Furnish furnish = (parts.length > 8 && !parts[8].isEmpty()) ? Furnish.valueOf(parts[8]) : null;
        View view = (parts.length > 9 && !parts[9].isEmpty()) ? View.valueOf(parts[9]) : View.GOOD;

        // Дом (может быть null)
        House house = null;
        if (parts.length > 10 && !parts[10].isEmpty()) {
            String houseName = parts[10];
            Long year = (parts.length > 11 && !parts[11].isEmpty()) ? Long.parseLong(parts[11]) : null;
            Long floors = (parts.length > 12 && !parts[12].isEmpty()) ? Long.parseLong(parts[12]) : null;
            Integer flatsOnFloor = (parts.length > 13 && !parts[13].isEmpty()) ? Integer.parseInt(parts[13]) : null;
            Long lifts = (parts.length > 14 && !parts[14].isEmpty()) ? Long.parseLong(parts[14]) : null;

            house = new House(houseName, year, floors, flatsOnFloor, lifts);
        }

        // Сначала создаём объект, потом устанавливаем дату через setter
        Flat flat = new Flat(name, coordinates, area, numberOfRooms, numberOfBathrooms, furnish, view, house);
        flat.setId(id);
        flat.setCreationDate(creationDate);  // Устанавливаем дату отдельно

        return flat;
    }

    /**
     * Преобразовать список Flat в список CSV строк.
     */
    public List<String> toCsvLines(List<Flat> flats) {
        List<String> lines = new ArrayList<>();
        for (Flat flat : flats) {
            lines.add(toCsv(flat));
        }
        return lines;
    }

    /**
     * Преобразовать список CSV строк в список Flat.
     */
    public List<Flat> fromCsvLines(List<String> lines) {
        List<Flat> flats = new ArrayList<>();
        for (String line : lines) {
            if (!line.isEmpty()) {
                flats.add(fromCsv(line));
            }
        }
        return flats;
    }
}