package com.myproj.netty.provider2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.SneakyThrows;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author shenxie
 * @date 2021/2/20
 */
public class MillionProvider {

    private static final int START_PORT = 8000;

    private static final int STOP_PORT = 9000;

    public static void main(String[] args) {
        startServer();
    }


    @SneakyThrows
    private static void startServer(){
        System.out.println("服务启动中。。。。。。");
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.SO_REUSEADDR, true)
                .childHandler(new ConnectionCountHandler());


        for (int i = 0; i <= STOP_PORT - START_PORT; i++) {
            final int port = START_PORT + i;
            bootstrap.bind(port)
                    .addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {
                            System.out.println("成功绑定监听端口：" + port);
                        }
                    });
        }
        System.out.println("服务端已启动");

        TimeUnit.MINUTES.sleep(1);
    }

    /**
     * 每隔2s统计一次，当前客户端的连接数
     */
    @ChannelHandler.Sharable
    static class ConnectionCountHandler extends ChannelInboundHandlerAdapter {

        private final AtomicInteger count = new AtomicInteger();

        public ConnectionCountHandler() {
            Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(() ->{
                System.out.println("当前客户端连接数：" + count.get());
            }, 0,2, TimeUnit.SECONDS);
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            count.getAndIncrement();
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            count.getAndDecrement();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
                throws Exception {
            ctx.fireExceptionCaught(cause);
            System.out.println("最终连接数： " + count.get());
            System.exit(0);
        }


    }



}
