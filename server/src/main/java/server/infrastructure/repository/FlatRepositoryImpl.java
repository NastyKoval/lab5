package server.infrastructure.repository;

import common.domain.enums.Furnish;
import common.domain.enums.View;
import common.domain.model.Coordinates;
import common.domain.model.Flat;
import common.domain.model.House;
import server.infrastructure.database.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Реализация репозитория для работы с PostgreSQL.
 * Хранит коллекцию в памяти и синхронизирует с базой данных.
 */
public class FlatRepositoryImpl implements FlatRepository {

    private final DatabaseManager dbManager;
    private final List<Flat> collection;

    public FlatRepositoryImpl(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        // создаём LinkedList и загружаем данные из БД
        this.collection = new LinkedList<>();
        this.collection.addAll(loadAllFromDatabase());
        fixNullOwnerIds();
    }

    @Override
    public List<Flat> findAll() {
        return collection; // Читаем только из памяти в бд не лезем
    }

    @Override
    public Optional<Flat> findById(int id) {
        return collection.stream()
                .filter(flat -> flat.getId() == id)
                .findFirst();
    }

    @Override
    public boolean existsById(int id) {
        return collection.stream()
                .anyMatch(flat -> flat.getId() == id);
    }

    @Override
    public int size() {
        return collection.size();
    }

    @Override
    public void save(Flat flat) {
        // Сначала пишем в БД
        // RETURNING id - просим базу вернуть id, который она сама сгенерировала из sequence
        String sql = """
                INSERT INTO flats (name, coord_x, coord_y, creation_date, area,
                                    number_of_rooms, number_of_bathrooms, furnish, view,
                                    house_name, house_year, house_number_of_floors,
                                    house_number_of_flats_on_floor, house_number_of_lifts, owner_id)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                RETURNING id
                """;

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            bindFlatToStatement(stmt, flat);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    long generatedId = rs.getLong("id");
                    flat.setId((int) generatedId);
                } else {
                    throw new SQLException("Не удалось получить id новой квартиры");
                }
            }
            // Только если SQL прошёл без ошибки - добавляем в память
            collection.add(flat);

        } catch (SQLException e) {
            System.err.println("Ошибка сохранения квартиры в БД: " + e.getMessage());
            throw new RuntimeException("Не удалось сохранить квартиру", e);
        }
    }

    @Override
    public void update(int id, Flat newFlat) {
        // owner_id в этот запрос не входит - владельца квартиры менять нельзя
        String sql = """
                UPDATE flats SET
                    name = ?, coord_x = ?, coord_y = ?, creation_date = ?, area = ?,
                    number_of_rooms = ?, number_of_bathrooms = ?, furnish = ?, view = ?,
                    house_name = ?, house_year = ?, house_number_of_floors = ?,
                    house_number_of_flats_on_floor = ?, house_number_of_lifts = ?
                WHERE id = ?
                """;

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            int lastIdx = bindFlatFieldsWithoutOwner(stmt, newFlat, 1);
            stmt.setInt(lastIdx, id); // последний параметр - id в WHERE

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new IllegalArgumentException("Элемент с ID=" + id + " не найден в БД");
            }

            // SQL прошёл успешно - обновляем коллекцию в памяти
            for (int i = 0; i < collection.size(); i++) {
                if (collection.get(i).getId() == id) {
                    newFlat.setId(id);
                    // сохраняем оригинального владельца, его update не меняет
                    newFlat.setOwnerId(collection.get(i).getOwnerId());
                    collection.set(i, newFlat);
                    return;
                }
            }

        } catch (SQLException e) {
            System.err.println("Ошибка обновления квартиры в БД: " + e.getMessage());
            throw new RuntimeException("Не удалось обновить квартиру", e);
        }
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM flats WHERE id = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                // удаление в БД прошло успешно - убираем и из памяти
                collection.removeIf(flat -> flat.getId() == id);
            }

        } catch (SQLException e) {
            System.err.println("Ошибка удаления квартиры из БД: " + e.getMessage());
            throw new RuntimeException("Не удалось удалить квартиру", e);
        }
    }

    @Override
    public int removeByOwnerId(int ownerId) {
        String sql = "DELETE FROM flats WHERE owner_id = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, ownerId);
            int removedCount = stmt.executeUpdate();

            // Обновляем кэш в памяти: удаляем только свои объекты
            collection.removeIf(flat -> flat.getOwnerId() != null && flat.getOwnerId() == ownerId);

            return removedCount;

        } catch (SQLException e) {
            System.err.println("Ошибка удаления квартир по владельцу: " + e.getMessage());
            throw new RuntimeException("Не удалось удалить квартиры", e);
        }
    }

    /**
     * Исправляет NULL owner_id для старых записей.
     * Вызывается один раз при старте сервера.
     */
    private void fixNullOwnerIds() {
        String sql = """
        UPDATE flats 
        SET owner_id = (SELECT id FROM users ORDER BY id LIMIT 1)
        WHERE owner_id IS NULL
        """;

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            int updated = stmt.executeUpdate();
            if (updated > 0) {
                System.out.println("Исправлено owner_id для " + updated + " квартир (было NULL)");
            }

        } catch (SQLException e) {
            System.err.println("Не удалось исправить NULL owner_id: " + e.getMessage());
        }
    }

    @Override
    public void clear() {
        String sql = "DELETE FROM flats";

        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);
            collection.clear();

        } catch (SQLException e) {
            System.err.println("Ошибка очистки таблицы flats: " + e.getMessage());
            throw new RuntimeException("Не удалось очистить коллекцию", e);
        }
    }

    @Override
    public void saveAll(List<Flat> flats) {
        // полная пересинхронизация: очищаем таблицу и заливаем заново одной транзакцией
        String deleteSql = "DELETE FROM flats";
        String insertSql = """
                INSERT INTO flats (id, name, coord_x, coord_y, creation_date, area,
                                    number_of_rooms, number_of_bathrooms, furnish, view,
                                    house_name, house_year, house_number_of_floors,
                                    house_number_of_flats_on_floor, house_number_of_lifts, owner_id)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = dbManager.getConnection()) {
            conn.setAutoCommit(false); // начинаем транзакцию: либо всё сохранится, либо ничего

            try (Statement clearStmt = conn.createStatement()) {
                clearStmt.execute(deleteSql);
            }

            try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                for (Flat flat : flats) {
                    stmt.setInt(1, flat.getId());
                    bindFlatToStatement(stmt, flat, 2);
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }

            conn.commit();

            collection.clear();
            collection.addAll(flats);

        } catch (SQLException e) {
            System.err.println("Ошибка полного сохранения коллекции: " + e.getMessage());
            throw new RuntimeException("Не удалось сохранить коллекцию", e);
        }
    }

    // Загружает коллекцию квартир из базы данных (вызывается один раз при старте сервера)
    private List<Flat> loadAllFromDatabase() {
        List<Flat> result = new LinkedList<>();
        String sql = "SELECT * FROM flats ORDER BY id";

        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                result.add(mapRowToFlat(rs));
            }
            System.out.println("Загружено квартир из БД: " + result.size());

        } catch (SQLException e) {
            System.err.println("Ошибка загрузки квартир из БД: " + e.getMessage());
            throw new RuntimeException("Не удалось загрузить коллекцию квартир", e);
        }
        return result;
    }

    // Заполняет параметры запроса полями Flat, включая owner_id в конце (для save/saveAll)
    private int bindFlatToStatement(PreparedStatement stmt, Flat flat, int startIdx) throws SQLException {
        int idx = bindFlatFieldsWithoutOwner(stmt, flat, startIdx);

        Integer ownerId = flat.getOwnerId();
        if (ownerId != null) {
            stmt.setInt(idx, ownerId);
        } else {
            stmt.setNull(idx, java.sql.Types.INTEGER);
        }
        return idx + 1;
    }

    private void bindFlatToStatement(PreparedStatement stmt, Flat flat) throws SQLException {
        bindFlatToStatement(stmt, flat, 1);
    }

    // Заполняет параметры запроса полями Flat без owner_id (для update - владельца не меняем)
    private int bindFlatFieldsWithoutOwner(PreparedStatement stmt, Flat flat, int startIdx) throws SQLException {
        int idx = startIdx;

        stmt.setString(idx++, flat.getName());
        stmt.setInt(idx++, flat.getCoordinates().getX());
        stmt.setFloat(idx++, flat.getCoordinates().getY());
        stmt.setTimestamp(idx++, Timestamp.valueOf(flat.getCreationDate()));
        stmt.setDouble(idx++, flat.getArea());

        // numberOfRooms может быть null - тогда пишем NULL в базу
        if (flat.getNumberOfRooms() != null) {
            stmt.setInt(idx++, flat.getNumberOfRooms());
        } else {
            stmt.setNull(idx++, java.sql.Types.INTEGER);
        }

        stmt.setLong(idx++, flat.getNumberOfBathrooms());

        if (flat.getFurnish() != null) {
            stmt.setString(idx++, flat.getFurnish().name());
        } else {
            stmt.setNull(idx++, java.sql.Types.VARCHAR);
        }

        stmt.setString(idx++, flat.getView().name());

        // house может быть null - тогда все его поля пишем как NULL
        House house = flat.getHouse();
        if (house != null) {
            stmt.setString(idx++, house.getName());
            stmt.setLong(idx++, house.getYear());
            stmt.setLong(idx++, house.getNumberOfFloors());
            if (house.getNumberOfFlatsOnFloor() != null) {
                stmt.setInt(idx++, house.getNumberOfFlatsOnFloor());
            } else {
                stmt.setNull(idx++, java.sql.Types.INTEGER);
            }
            if (house.getNumberOfLifts() != null) {
                stmt.setLong(idx++, house.getNumberOfLifts());
            } else {
                stmt.setNull(idx++, java.sql.Types.BIGINT);
            }
        } else {
            stmt.setNull(idx++, java.sql.Types.VARCHAR);
            stmt.setNull(idx++, java.sql.Types.BIGINT);
            stmt.setNull(idx++, java.sql.Types.BIGINT);
            stmt.setNull(idx++, java.sql.Types.INTEGER);
            stmt.setNull(idx++, java.sql.Types.BIGINT);
        }

        return idx;
    }

    // Превращает одну строку результата SQL-запроса в объект Flat
    private Flat mapRowToFlat(ResultSet rs) throws SQLException {
        Coordinates coordinates = new Coordinates(rs.getInt("coord_x"), rs.getFloat("coord_y"));

        // house_name == null значит у этой квартиры нет дома
        String houseName = rs.getString("house_name");
        House house = null;
        if (houseName != null) {
            house = new House(
                    houseName,
                    rs.getLong("house_year"),
                    rs.getLong("house_number_of_floors"),
                    rs.getObject("house_number_of_flats_on_floor") != null ? rs.getInt("house_number_of_flats_on_floor") : null,
                    rs.getObject("house_number_of_lifts") != null ? rs.getLong("house_number_of_lifts") : null
            );
        }

        String furnishStr = rs.getString("furnish");
        Furnish furnish = furnishStr != null ? Furnish.valueOf(furnishStr) : null;

        Flat flat = new Flat(
                rs.getString("name"),
                coordinates,
                rs.getDouble("area"),
                rs.getObject("number_of_rooms") != null ? rs.getInt("number_of_rooms") : null,
                rs.getLong("number_of_bathrooms"),
                furnish,
                View.valueOf(rs.getString("view")),
                house
        );

        flat.setId(rs.getInt("id"));
        flat.setCreationDate(rs.getTimestamp("creation_date").toLocalDateTime());
        int ownerIdFromDb = rs.getInt("owner_id");
        // Если в БД было NULL, rs.getInt возвращает 0; конвертируем в null(баг)
        flat.setOwnerId(rs.wasNull() ? null : ownerIdFromDb);

        return flat;
    }
}