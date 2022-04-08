package ru.geekbrains.Lesson1.NIO.Client;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class NIOClient {
    public static void main(String[] args) throws Exception {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress("localhost", 8189));
        System.out.println("Введите сообщение, для окончания общения введи: /stop");
        Scanner scanner = new Scanner(System.in);
        ByteBuffer buf = ByteBuffer.allocate(50);

        boolean flag = true;
        while (flag) {
            buf.clear();
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("/stop")) {
                flag = false;
            }
            input += "\n";
            buf.put(input.getBytes());
            buf.flip();
            socketChannel.write(buf);
            buf.clear();
            int read = socketChannel.read(buf);
            String readMessage = new String(buf.array(), 0, read);
            System.out.println("Ответ сервера" + readMessage);
        }
        socketChannel.close();

    }
}
