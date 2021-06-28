package com.myproj.netty.consumer.proxy;

import com.myproj.netty.api.IRpcService;
import com.myproj.netty.protocol.InvokerProtocol;
import com.myproj.netty.provider.RpcServiceImpl;
import com.myproj.netty.registy.RegistryHandler;
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
import lombok.Data;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * RPC调用的代理类
 * @author shenxie
 * @date 2021/1/24
 */
public class RpcProxy {

    /**
     * 反射 创建代理
     * @param clazz 接口
     * @return
     */
    public static <T> T create(Class<?> clazz) {
        MethodProxy proxy = new MethodProxy(clazz);

        Class<?>[] interfaces = clazz.isInterface() ? new Class[]{clazz} : clazz.getInterfaces();

        T result  = (T) Proxy.newProxyInstance(clazz.getClassLoader(), interfaces, proxy);
        return result;

    }

    @Data
    private static class MethodProxy implements InvocationHandler{

        private Class<?> clazz;

        public MethodProxy(Class<?> clazz) {
            this.clazz = clazz;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            if(Object.class.equals(method.getDeclaringClass())) {
                return method.invoke(this, args);
            }else{
                // 如果传入的是一个接口
                return rpcInvoke( method , args);
            }
        }

        /**
         * 实现接口的核心方法
         * @param method
         * @param args
         * @return
         */
        public Object rpcInvoke(Method method , Object[] args) throws InterruptedException {
            // 传输协议封装
            InvokerProtocol msg = new InvokerProtocol();
            msg.setClassName(this.clazz.getName());
            msg.setMethodName(method.getName());
            msg.setValue(args);
            msg.setParams(method.getParameterTypes());

            RpcProxyHandler consumerHandler = new RpcProxyHandler();
            EventLoopGroup group = new NioEventLoopGroup();

            Bootstrap b = new Bootstrap();

            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY,true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            // 协议的编码器
                            pipeline.addLast("frameDecoder",new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0 ,4, 0 ,4));
                            // 协议解码器
                            pipeline.addLast("frameEncoder",new LengthFieldPrepender(4));
                            // 对象参数类型的编码器
                            pipeline.addLast("encoder", new ObjectEncoder());
                            // 对象参数类型的解码器
                            pipeline.addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
                            pipeline.addLast("handler",consumerHandler);
                        }
                    });

            ChannelFuture future = b.connect("localhost", 8080).sync();
            future.channel().writeAndFlush(msg).sync();
            future.channel().closeFuture().sync();

            return consumerHandler.getResponse();
        }
    }
}
