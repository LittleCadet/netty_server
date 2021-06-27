package com.myproj.netty.provider;

import com.myproj.netty.api.IRpcHelloService;

/**
 * @author shenxie
 * @date 2021/1/24
 */
public class RpcHelloServiceImpl implements IRpcHelloService {
    @Override
    public String hello(String name) {
        return name;
    }
}
