package com.myproj.netty.buffer;

import java.nio.IntBuffer;

/**
 * @author shenxie
 * @date 2021/1/15
 */
public class IntBufferDemo {

    /**
     * intBuffer底层实现为数组。【不能超过数组长度，不然会越界】
     */
    public static void main(String[] args) {
        IntBuffer allocate = IntBuffer.allocate(10);
        for (int i = 0; i< 10 ; i++) {
            allocate.put(i * 2);
        }

        allocate.flip();

        while(allocate.hasRemaining()) {
            System.out.println(allocate.get());
        }
    }
}
