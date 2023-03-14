package org.peggy.atoms;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * 关于 Atome 与 Sychronizeds 与 Adder 之间的性能测试 (这个测试类有点问题)
 * 通过下面的模拟可以看出,在高并发的环境下
 * LongAdder 的执行效率要比 AtomicLong 的执行效率要高
 * 因为 LongAdder 的底层使用的一种分段 cas 锁
 *
 * @author peggy
 * @date 2023-03-13 13:37
 */
public class AtomeVsSyncVsLongAdder {

    static long count1 = 0L;
    static AtomicLong count2 = new AtomicLong(0L);
    static LongAdder longAdder = new LongAdder();


    //创建线程并放置到数组中
    public static void main(String[] args) {
        Object lock = new Object();

        //通过 synchronized 加锁占用的时间
        Thread[] threads = new Thread[1000];
        for (int i = 0; i < threads.length; i++) {
            threads[i] =
                    new Thread(() -> {
                        for (int k = 0; k < 100000; k++) {
                            synchronized (lock) {
                                count1++;
                            }
                        }
                    });
        }
        long start = System.currentTimeMillis();
        for (Thread thread : threads) {
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("count1:" + count1 + "==>" + (end - start));

        //AtomicLong 所用总时间
        for (int i = 0; i < threads.length; i++) {
            threads[i] =
                    new Thread(() -> {
                        for (int k = 0; k < 100000; k++) count2.incrementAndGet();
                    });
        }

        start = System.currentTimeMillis();
        for (Thread t : threads) {
            t.start();
            try {
                //加入 join 的作用是防止, main 线程在其他线程之前提前结束,从而导致程序提前的终止
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        //当上面的所有的线程结束 main 线程继续
        end = System.currentTimeMillis();
        System.out.println("AtomicLong:" + count2 + "==>" + (end - start));

        //通过 longAdder 加锁占用
        //AtomicLong 所用总时间
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                for (int k = 0; k < 100000; k++) {
                    longAdder.add(1);
                }
            });
        }

        start = System.currentTimeMillis();
        for (Thread t : threads) {
            t.start();
            try {
                //加入 join 的作用是防止, main 线程在其他线程之前提前结束,从而导致程序提前的终止
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        //当上面的所有的线程结束 main 线程继续
        end = System.currentTimeMillis();
        System.out.println("longAdder:" + longAdder + "==>" + (end - start));

    }
}
