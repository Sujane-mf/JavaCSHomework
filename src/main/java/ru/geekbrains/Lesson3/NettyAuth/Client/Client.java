package ru.geekbrains.Lesson3.NettyAuth.Client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import ru.geekbrains.Lesson3.NettyAuth.Common.Handler.JsonDecoder;
import ru.geekbrains.Lesson3.NettyAuth.Common.Handler.JsonEncoder;
import ru.geekbrains.Lesson3.NettyAuth.Common.Message.AuthMessage;
import ru.geekbrains.Lesson3.NettyAuth.Common.Message.DateMessage;
import ru.geekbrains.Lesson3.NettyAuth.Common.Message.Message;
import ru.geekbrains.Lesson3.NettyAuth.Common.Message.TextMessage;

import java.time.LocalDateTime;
import java.util.Date;

public class Client {
    public static void main(String[] args) throws InterruptedException {
    new Client().start();
}

        public void start() {
            final NioEventLoopGroup group = new NioEventLoopGroup();
            try {
                Bootstrap bootstrap = new Bootstrap()
                        .group(group)
                        .channel(NioSocketChannel.class)
                        .option(ChannelOption.SO_KEEPALIVE, true)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) {
                                ch.pipeline().addLast(
                                        new LengthFieldBasedFrameDecoder(1024 * 1024, 0, 3, 0, 3),
                                        new LengthFieldPrepender(3),
                                        new JsonDecoder(),
                                        new JsonEncoder(),
                                        new SimpleChannelInboundHandler<Message>() {
                                            @Override
                                            protected void channelRead0(ChannelHandlerContext ctx, Message msg) {
                                                System.out.println("receive msg " + msg);
                                            }
                                        }
                                );
                            }
                        });

                System.out.println("Client started");

                ChannelFuture channelFuture = bootstrap.connect("localhost", 8189).sync();
                while (channelFuture.channel().isActive()) {
//                    TextMessage textMessage = new TextMessage();
//                    textMessage.setText(String.format("[%s] %s", LocalDateTime.now(), Thread.currentThread().getName()));
//                    System.out.println("Try to send message: " + textMessage);
//
//                    channelFuture.channel().writeAndFlush(textMessage);
//
//                    DateMessage dateMessage = new DateMessage();
//                    dateMessage.setDate(new Date());
//                    channelFuture.channel().write(dateMessage);
//                    channelFuture.channel().flush();

                    AuthMessage auth = new AuthMessage();
                    auth.setLogin("Jane");
                    auth.setPassword("1111");
                    channelFuture.channel().write(auth);
                    channelFuture.channel().flush();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                group.shutdownGracefully();
            }
        }
}

