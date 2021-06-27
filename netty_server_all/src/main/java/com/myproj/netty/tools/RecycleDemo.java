package com.myproj.netty.tools;

import io.netty.util.Recycler;

/**
 * netty的对象回收站的demo
 * @author shenxie
 * @date 2021/2/19
 */
public class RecycleDemo {

    private static final Recycler<Order> RECYCLER = new Recycler<Order>() {
        @Override
        protected Order newObject(Handle<Order> handle) {
            return new Order(handle);
        }
    };

    static class Order {
        private final Recycler.Handle<Order> handle;

        Order(Recycler.Handle<Order> handle) {
            this.handle = handle;
        }

        public void recycle(){
            handle.recycle(this);
        }
    }

    public static void main(String[] args) {
        Order order1 = RECYCLER.get();
        // 将Order对象放入对象回收站中
        order1.recycle();
        // 从对象回收站中取出Order对象
        Order order2 = RECYCLER.get();

        // 如果： true： 说明： order2是order1放入对象回收站的结果
        System.out.println(order1 == order2);
    }
}
