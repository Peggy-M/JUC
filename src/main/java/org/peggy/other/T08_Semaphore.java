package org.peggy.other;

import java.util.concurrent.Semaphore;

/**
 * 信号量 Semaphore 同时允许有多个线程在运行
 * 应用场景
 *  - 限流
 *  - 车道与收费站
 *  - 购票 （例如余票只有 5 个只允许并发的只有 5 个 线程）
 * @author peggy
 * @date 2023-03-14 15:49
 */
public class T08_Semaphore {
    public static void main(String[] args) {
        /**
         * 当这里设置为 1 的时候此时,只限制允许一个线程执行
         * 当这里设置为 2 的时候,此时允许两个线程同时执行
         * 在这里很多人都认为这个与线程池是相同的
         *
         * 线程池是自动创建线程的
         *  而对于 Semaphore 是需要我自己手动创建线程的
         *  可参考该连接 https://blog.csdn.net/bobozai86/article/details/114004451
         */
        Semaphore semaphore = new Semaphore(1);

        new Thread(() -> {
            try {
                semaphore.acquire();
                System.out.println("线程A 在执行中。。。");
                Thread.sleep(1000);
                System.out.println("线程A 执行完毕。。。");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }finally {
                semaphore.release();
            }
        },"A").start();

        new Thread(() -> {
            try {
                semaphore.acquire();
                System.out.println("线程B 在执行中。。。");
                Thread.sleep(1000);
                System.out.println("线程B 执行完毕。。。");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }finally {
                semaphore.release();
            }
        },"B").start();
    }
}
