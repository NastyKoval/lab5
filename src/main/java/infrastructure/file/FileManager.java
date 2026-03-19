package infrastructure.file;

import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Класс для работы с файлами
 * Читает и записывает данные в CSV формате
 */
public class FileManager {

    private final String fileName;

    public FileManager(String fileName) {
        this.fileName = fileName;
        createFileIfNotExists();
    }

    /**
     * Прочитать все строки из файла
     * @return список строк
     */
    public List<String> readAllLines() {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка чтения файла: " + e.getMessage());
        }
        return lines;
    }

    /**
     * Записать строки в файл
     * @param lines список строк для записи
     */
    public void writeAllLines(List<String> lines) {
        try (BufferedOutputStream bos = new BufferedOutputStream(
                new FileOutputStream(fileName))) {
            for (String line : lines) {
                bos.write((line + "\n").getBytes());
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка записи файла: " + e.getMessage());
        }
    }

    /**
     * Проверить существование файла
     * @return true если файл существует
     */
    public boolean fileExists() {
        File file = new File(fileName);
        return file.exists();
    }

    /**
     * Создать файл если не существует
     */
    private void createFileIfNotExists() {
        if (!fileExists()) {
            try {
                File file = new File(fileName);

                // Создаём родительские директории если нет
                File parentDir = file.getParentFile();
                if (parentDir != null && !parentDir.exists()) {
                    parentDir.mkdirs();
                }

                // Создаём сам файл
                file.createNewFile();

            } catch (IOException e) {
                throw new RuntimeException("Не удалось создать файл: " + e.getMessage());
            }
        }
    }
}