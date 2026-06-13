package server.connection;

import common.request.Request;
import common.response.Response;
import server.application.context.UserContext;
import server.application.service.FlatService;
import server.application.command.CommandRegistryServer;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UdpServer {

    private final int port;
    private final FlatService flatService;
    private final CommandRegistryServer commandRegistry;
    private final ThreadPoolManager threadPoolManager;

    // Замок для синхронизации доступа к коллекции/БД
    private final Object collectionLock = new Object();

    private volatile boolean running = true;
    private final byte[] buffer = new byte[65507];

    public UdpServer(int port, FlatService flatService, ThreadPoolManager threadPoolManager) {
        this.port = port;
        this.flatService = flatService;
        this.commandRegistry = new CommandRegistryServer(flatService);
        this.threadPoolManager = threadPoolManager;
    }

    public void start() {
        try (DatagramSocket socket = new DatagramSocket(port)) {
            System.out.println("Многопоточный сервер запущен на порту " + port);

            while (running) {
                try {
                    // Ждём пакет от клиента (блокирующая операция)
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);

                    // чтение - (Fixed Pool)
                    threadPoolManager.getReceivePool().submit(() -> {
                        try {
                            Request request = deserialize(packet.getData(), packet.getLength());
                            System.out.println("Получено: " + request.getCommandName());

                            //  обработка - (Cached Pool)
                            threadPoolManager.getProcessPool().submit(() -> {
                                try {

                                    synchronized (collectionLock) {
                                        Response response = commandRegistry.execute(request);

                                        // отпрвка - (Fixed Pool)
                                        threadPoolManager.getSendPool().submit(() -> {
                                            try {
                                                byte[] responseData = serialize(response);
                                                DatagramPacket responsePacket = new DatagramPacket(
                                                        responseData, responseData.length,
                                                        packet.getAddress(), packet.getPort()
                                                );
                                                socket.send(responsePacket);
                                                System.out.println("Ответ отправлен");
                                            } catch (IOException e) {
                                                System.err.println("Ошибка отправки: " + e.getMessage());
                                            }
                                        });
                                    }
                                } catch (Exception e) {
                                    System.err.println("Ошибка обработки: " + e.getMessage());
                                    sendError(socket, packet, e.getMessage());
                                } finally {
                                    // Очищаем контекст пользователя после обработки
                                    UserContext.clear();
                                }
                            });

                        } catch (Exception e) {
                            System.err.println("Ошибка десериализации: " + e.getMessage());
                            sendError(socket, packet, "Ошибка чтения запроса");
                        }
                    });

                } catch (IOException e) {
                    if (running) {
                        System.err.println("Ошибка сети: " + e.getMessage());
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Критическая ошибка сервера: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /** Вспомогательный метод: отправить ошибку клиенту */
    private void sendError(DatagramSocket socket, DatagramPacket packet, String message) {
        try {
            Response errorResponse = new Response(false, message, null);
            byte[] errorData = serialize(errorResponse);
            DatagramPacket errorPacket = new DatagramPacket(
                    errorData, errorData.length,
                    packet.getAddress(), packet.getPort()
            );
            socket.send(errorPacket);
        } catch (IOException e) {
            System.err.println("Не удалось отправить ошибку: " + e.getMessage());
        }
    }


    private Request deserialize(byte[] data, int length) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data, 0, length);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            return (Request) ois.readObject();
        }
    }


    private byte[] serialize(Response response) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(response);
            oos.flush();
            return baos.toByteArray();
        }
    }

    public void stop() {
        running = false;
        threadPoolManager.shutdown();
        System.out.println("Сервер остановлен");
    }
}