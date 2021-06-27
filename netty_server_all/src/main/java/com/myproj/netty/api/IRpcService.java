package com.myproj.netty.api;

/**
 * @author shenxie
 * @date 2021/1/24
 */
public interface IRpcService {

    int add(int a, int b);

    int sub(int a, int b);

    int mult(int a, int b);

    int div(int a, int b);
}
