package infrastructure.file;

import domain.enums.Furnish;
import domain.enums.View;
import domain.model.Coordinates;
import domain.model.Flat;
import domain.model.House;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс для парсинга объектов Flat в CSV и обратно
 * Преобразует объекты в строки и строки в объекты
 */
public class CsvParser {

    private static final String DELIMITER = ",";
    /**
     * Преобразовать объект Flat в CSV строку
     * @param flat объект для преобразования
     * @return строка в формате CSV
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
        sb.append(flat.getNumberOfBathrooms()).append(DELIMITER);

        sb.append(flat.getFurnish() != null ? flat.getFurnish().name() : "").append(DELIMITER);
        sb.append(flat.getView().name()).append(DELIMITER);

        House house = flat.getHouse();
        if (house != null) {
            sb.append(house.getName()).append(DELIMITER);
            sb.append(house.getYear()).append(DELIMITER);
            sb.append(house.getNumberOfFloors()).append(DELIMITER);
            sb.append(house.getNumberOfFlatsOnFloor()).append(DELIMITER);
            sb.append(house.getNumberOfLifts());
        } else {
            sb.append(",,,,");
        }

        return sb.toString();

    }

    /**
     * Преобразовать CSV строку в объект Flat
     * @param line строка в формате CSV
     * @return объект Flat
     */
    public Flat fromCsv(String line) {
        String[] parts = line.split(DELIMITER);


        int id = Integer.parseInt(parts[0]);
        String name = parts[1];

        Integer x = Integer.parseInt(parts[2]);
        float y = Float.parseFloat(parts[3]);
        Coordinates coordinates = new Coordinates(x, y);

        LocalDateTime creationDate = LocalDateTime.parse(parts[4]);

        Double area = Double.parseDouble(parts[5]);

        Integer numberOfRooms = parts[6].isEmpty() ? null : Integer.parseInt(parts[6]);

        long numberOfBathrooms = Long.parseLong(parts[7]);

        Furnish furnish = parts[8].isEmpty() ? null : Furnish.valueOf(parts[8]);

        View view = View.valueOf(parts[9]);

        // Дом (может быть null)
        House house = null;
        if (parts.length > 10 && !parts[10].isEmpty()) {
            Long year = parts[11].isEmpty() ? null : Long.parseLong(parts[11]);
            Long floors = parts[12].isEmpty() ? null : Long.parseLong(parts[12]);
            Integer flatsOnFloor = parts[13].isEmpty() ? null : Integer.parseInt(parts[13]);
            Long lifts = parts[14].isEmpty() ? null : Long.parseLong(parts[14]);

            house = new House(parts[10], year, floors, flatsOnFloor, lifts);
        }

        // Flat без id и creationDate
        Flat flat = new Flat(name, coordinates, area, numberOfRooms,
                numberOfBathrooms, furnish, view, house);
        flat.setId(id);
        flat.setCreationDate(creationDate);

        return flat;
    }

    /**
     * Преобразовать список Flat в список CSV строк
     * @param flats список объектов
     * @return список строк
     */
    public List<String> toCsvLines(List<Flat> flats) {
        List<String> lines = new ArrayList<>();
        for (Flat flat : flats) {
            lines.add(toCsv(flat));
        }
        return lines;
    }

    /**
     * Преобразовать список CSV строк в список Flat
     * @param lines список строк
     * @return список объектов
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