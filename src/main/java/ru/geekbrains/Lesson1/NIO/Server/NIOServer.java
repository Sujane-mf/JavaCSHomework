package ru.geekbrains.Lesson1.NIO.Server;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NIOServer {
    public static void main(String[] args) throws Exception {
        ExecutorService es = Executors.newFixedThreadPool(5);
        Selector selector = Selector.open();
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(8189));
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println ("Сервер запущен");

        while (selector.select() > 0) {
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = keys.iterator();
            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                if (key.isAcceptable()) {
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    if (socketChannel != null) {
                        es.submit(new TaskHandler(socketChannel));
                    }
                }
            }
        }

    }
    private static class TaskHandler implements Runnable {
        private SocketChannel clientChannel;
        public TaskHandler(SocketChannel clientChannel) {
            this.clientChannel = clientChannel;
        }


        @Override
        public void run() {
            ByteBuffer buf = ByteBuffer.allocate(256);
            try {
                boolean flag = true;
                while (flag) {
                    buf.clear();
                    int read = clientChannel.read(buf);
                    String readMessage = new String(buf.array(), 0, read);
                    String writeMessage = "Echo: " + readMessage + "\n";
                    if ("/stop".equalsIgnoreCase(readMessage)) {
                        writeMessage = "Общение завершено" + "\n";
                        flag = true;
                    }


                    buf.clear();
                    buf.put(writeMessage.getBytes());
                    buf.flip ();
                    clientChannel.write(buf);
                }
                clientChannel.close();

            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

