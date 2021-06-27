package com.myproj.netty.tools;

import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.concurrent.FastThreadLocalThread;

import java.util.concurrent.TimeUnit;

/**
 * netty中关于FastThreadLocal的demo
 * @author shenxie
 * @date 2021/2/19
 */
public class FastThreadLocalDemo {

    private final FastThreadLocalTest fastThreadLocalTest;

    public FastThreadLocalDemo() {
        this.fastThreadLocalTest = new FastThreadLocalTest();
    }

    class FastThreadLocalTest extends FastThreadLocal<Object>{

        /**
         * 在get时，根据索引取到的值是UNSET时，会调用initialValue的方法。并将值放入底层数组中
         * @return
         * @throws Exception
         */
        @Override
        protected Object initialValue() throws Exception {
            return new Object();
        }
    }


    @SuppressWarnings("all")
    public static void main(String[] args) {
        FastThreadLocalDemo fastThreadLocalDemo = new FastThreadLocalDemo();
        // 普通线程: 使用IntenalThreadLocalMap中的slowGet
//        new Thread(() -> {
//            for (int i = 0; i < 100; i++) {
//                fastThreadLocalDemo.fastThreadLocalTest.set(new Object());
//                try {
//                    TimeUnit.SECONDS.sleep(1);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//
//        new Thread(() -> {
//            Object obj = fastThreadLocalDemo.fastThreadLocalTest.get();
//            for (int i = 0; i < 100; i++) {
//                System.out.println(obj == fastThreadLocalDemo.fastThreadLocalTest.get());
//            }
//
//        }).start();

        // FastThreadLocalThread 激活 InternalThreadLocalMap中fastGet的使用
        new FastThreadLocalThread(() -> {
            for (int i = 0; i < 100; i++) {
                fastThreadLocalDemo.fastThreadLocalTest.set(new Object());
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new FastThreadLocalThread(() -> {
            Object obj = fastThreadLocalDemo.fastThreadLocalTest.get();
            for (int i = 0; i < 100; i++) {
                System.out.println(obj == fastThreadLocalDemo.fastThreadLocalTest.get());
            }

        }).start();
    }


}
