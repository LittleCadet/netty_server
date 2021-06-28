package com.myproj.netty.provider.server;

import com.myproj.netty.protocol.InvokerProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.SneakyThrows;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author shenxie
 * @date 2021/1/24
 */
public class InvokeHandler extends ChannelInboundHandlerAdapter {

    /**
     * 保存所有可用服务
     */
    private static ConcurrentHashMap<String,Object> registryMap = new ConcurrentHashMap<>();

    /**
     * 保存所有相关的服务类
     */
    private static List<String> classNames;

    public InvokeHandler() {
        scannerClass("com.myproj.netty.provider");
        doRegister();
    }

    @SneakyThrows
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Object result = new Object();
        classNames = new ArrayList<>();
        InvokerProtocol request = (InvokerProtocol)msg;

        if(registryMap.containsKey(request.getClassName())) {
            Object clazz = registryMap.get(request.getClassName());
            clazz.getClass().getMethod(request.getMethodName(), request.getParams());
            Method method = clazz.getClass().getMethod(request.getMethodName(), request.getParams());
            result = method.invoke(clazz, request.getValue());
        }

        ctx.write(result);
        ctx.flush();
        ctx.close();
    }

    /**
     * 异常处理
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx , Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    /**
     * 递归扫描目录
     * @param packageName
     */
    private void scannerClass(String packageName) {
        URL url = this.getClass().getClassLoader().getResource(packageName.replaceAll("\\.", "/"));

        File dir = new File(url.getFile());

        Arrays.asList(dir.listFiles()).forEach(file -> {
            if(file.isDirectory()){
                if( ! file.getName().endsWith("server")){
                    scannerClass(packageName + "." + file.getName());
                }
            }else {
                classNames.add(packageName + "." + file.getName().replace(".class" , "").trim());
            }
        });
    }

    /**
     * 注册
     */
    private void doRegister(){

        classNames.forEach(className -> {
            try {
                Class<?> clazz = Class.forName(className);
                Class<?> i = clazz.getInterfaces()[0];
                registryMap.put(i.getName() , clazz.newInstance());
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        });
    }


}
