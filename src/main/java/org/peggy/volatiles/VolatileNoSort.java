package org.peggy.volatiles;

import java.util.ArrayList;

/**
 * Volatile 无法保证其原子性
 * @author peggy
 * @date 2023-03-10 20:30
 */
public class VolatileNoSort {

    private volatile int num = 0;

    //如果通过 synchronized 加锁,那么是可以保证其原子性的
    public /*synchronized*/ void add() {
        for (int i = 0; i < 10000; i++) num++;
    }

    public static void main(String[] args) {

        ArrayList<Thread> threads = new ArrayList<>();

        VolatileNoSort vo = new VolatileNoSort();

        for (int i = 0; i < 10; i++) {
            threads.add(new Thread(vo::add));
        }

        threads.forEach(Thread::start);

        threads.forEach(o -> {
            try {
                o.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        System.out.println(vo.num);
    }
}
