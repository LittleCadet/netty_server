package com.myproj.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

/**
 * @author shenxie
 * @date 2021/1/27
 */
public class ChatClient {

    /**
     * 客户端的连接
     * @return client
     */
    public ChatClient connect(String host, int port, String nickName) {

        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap
                    // 初始化客户端的Nio socketChannel
                    .channel(NioSocketChannel.class)
                    .group(group)
                    // 设置长连接
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    // 处理数据的handler
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            System.out.println(nickName + "开始initChannel!");
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0 ,4, 0 ,4));
                            pipeline.addLast(new LengthFieldPrepender(4));
                            pipeline.addLast("encoder", new ObjectEncoder());
                            pipeline.addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
                        }
                    });

            // 发起同步TCP连接操作 [UDP连接使用bind]
            // 建立连接时，才会调用ReflectiveChannelFactory的newChannel()
//            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            ChannelFuture channelFuture = bootstrap.bind(host,port).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }

        return this;
    }

    public static void main(String[] args) {
        String host = System.getProperty("host", "127.0.0.1");
        int port = Integer.parseInt(System.getProperty("port", "8009"));
        new ChatClient().connect(host, port, "LittleCadet");
    }
}
