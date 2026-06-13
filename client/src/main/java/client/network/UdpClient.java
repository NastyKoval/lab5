package client.network;

import common.request.Request;
import common.response.Response;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UdpClient {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;
    private static final int BUFFER_SIZE = 65507;

    // Отправляет запрос и возвращает ответ от сервера
    public static Response send(Request request) {
        try (DatagramSocket socket = new DatagramSocket()) {
            // Сериализует запрос в байты
            byte[] requestData = serialize(request);

            // Отправляет на сервер
            InetAddress address = InetAddress.getByName(SERVER_HOST);
            DatagramPacket sendPacket = new DatagramPacket(requestData, requestData.length, address, SERVER_PORT);
            socket.send(sendPacket);

            // Ждём ответ
            byte[] buffer = new byte[BUFFER_SIZE];
            DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
            socket.receive(receivePacket); //  Блокируется, пока сервер не ответит

            // Десериализует ответ
            return deserialize(receivePacket.getData(), receivePacket.getLength());
        } catch (Exception e) {
            System.err.println(" Ошибка связи с сервером: " + e.getMessage());
            return new Response(false, "Сервер недоступен", null);
        }
    }

    private static byte[] serialize(Object obj) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(obj);
            return baos.toByteArray();
        }
    }

    private static Response deserialize(byte[] data, int length) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data, 0, length);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            return (Response) ois.readObject();
        }
    }
}