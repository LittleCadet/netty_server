package com.myproj.netty.api;

/**
 * @author shenxie
 * @date 2021/1/24
 */
public interface IRpcHelloService {

    /**
     * 测试服务是否可用
     * @param name name
     * @return String
     */
    String hello(String name );
}
