package ru.geekbrains.Lesson2.SimpleNetty.Server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.ReferenceCountUtil;


public class NettyServer {
    private final int port;

    public static void main(String[] args) throws InterruptedException {
        new NettyServer(8189).start();
    }

    public NettyServer(int port) {
        this.port = port;
    }

    public void start() throws InterruptedException {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap server = new ServerBootstrap();
            server
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                            nioSocketChannel.pipeline().addLast(
                                    new LineBasedFrameDecoder(256),
                                    new StringDecoder(),
                                    new StringEncoder(),
                                    new SimpleChannelInboundHandler<String>() {
                                        @Override
                                        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
                                            System.out.println("Канал зарегистрирован");
                                        }

                                        @Override
                                        public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
                                            System.out.println("Канал разрегистрирован");
                                        }

                                        @Override
                                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                            System.out.println("Канал активирован");
                                        }

                                        @Override
                                        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                            System.out.println("Канал деактивирован");
                                        }

                                        @Override
                                        protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                                            ctx.writeAndFlush(msg);
                                        }
                                    }
                            );
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture future = server.bind(port).sync();
            System.out.println("Server started");
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
