package com.myproj.netty.consumer2;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import javax.naming.InitialContext;
import java.util.concurrent.ExecutionException;

/**
 * @author shenxie
 * @date 2021/2/20
 */
public class MillionConsumer {

    private static final int START_PORT = 8000;

    private static final int END_PORT = 9000;

    public static void main(String[] args) {
        connect();
    }

    private static void connect() {
        int count = 0;
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap()
                .group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {

                    }
                })
                .option(ChannelOption.SO_REUSEADDR, true);

        while (!Thread.interrupted()) {
            try {
                count ++ ;
                for (int i = 0; i <= END_PORT - START_PORT; i++) {
                    final int port = START_PORT + i == END_PORT? START_PORT : START_PORT + i;
//                    if( port != 8133) {
                        ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", port)
                                .addListener(new ChannelFutureListener() {
                                    @Override
                                    public void operationComplete(ChannelFuture future) throws Exception {
                                        if (!future.isSuccess()) {
                                            System.out.println("连接异常: port： " + port);
                                            System.exit(0);
                                        }
                                    }
                                });
                        channelFuture.get();
//                    }


                }
                System.out.println(String.format("第%s遍循环完成", count));
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

    }
}
