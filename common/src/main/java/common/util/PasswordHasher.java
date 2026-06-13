package common.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Утилита для хеширования паролей алгоритмом SHA-384.
 */
public class PasswordHasher {

    /**
     * Хеширует пароль используя SHA-384.
     *
     * @param password пароль в открытом виде
     * @return хеш пароля в виде hex строки
     */
    public static String hash(String password) {
        try {

            MessageDigest md = MessageDigest.getInstance("SHA-384");

            // Преобразуем пароль в байты и хешируем
            byte[] hashedBytes = md.digest(password.getBytes());

            // Конвертируем байты в hex строку
            return bytesToHex(hashedBytes);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-384 алгоритм не найден", e);
        }
    }

    /**
     * Проверяет совпадение пароля с хешем.
     *
     * @param password пароль в открытом виде
     * @param hashedPassword сохранённый хеш
     * @return true если пароли совпадают
     */
    public static boolean verify(String password, String hashedPassword) {
        String hashedInput = hash(password);
        return hashedInput.equals(hashedPassword);
    }

    /**
     * Преобразует массив байт в hex строку.
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}