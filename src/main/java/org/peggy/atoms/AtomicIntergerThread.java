package org.peggy.atoms;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 关于 Atomic 内部的实现的原理
 *  Atomic 的内部调用的
 *  this.compareAndSwapInt(var1, var2, var5, var5 + var4)
 *  cas(V,Expected,NewValue)
 *  -CPU 原语支持,一旦开始就无法打断
 * @author peggy
 * @date 2023-03-10 21:48
 */
public class AtomicIntergerThread {
    AtomicInteger num = new AtomicInteger();

    public void addNum() {
        for (int j = 0; j < 10000; j++) {
            num.incrementAndGet();
        }
    }

    public static void main(String[] args) {

        ArrayList<Thread> threads = new ArrayList<>();

        AtomicIntergerThread at = new AtomicIntergerThread();

        for (int i = 0; i < 10; i++) {
            threads.add(new Thread(at::addNum));
        }

        threads.forEach(o -> o.start());
        threads.forEach(o -> {
            try {
                o.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        System.out.println(at.num);
    }

}
