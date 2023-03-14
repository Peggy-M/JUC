package org.peggy.other;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * @author peggy
 * @date 2023-03-13 18:26
 */
public class T05_CyclicBarrier {
    public static void main(String[] args) {
        //到 20 个的时候就停止阻塞当前的线程
//                CyclicBarrier barrie = new CyclicBarrier(20);
        //当前的线程创建执行的数量达到了 20 个每次执行 barrie.await() 方法都会执行 CyclicBarrier 中的方法
        CyclicBarrier barrie = new CyclicBarrier(10, () -> {
            System.out.println("已经超出最大上限。。。。");
        });
        for (int i = 0; i < 20; i++) {
            new Thread(() -> {
                //每一个线程执行到最后调用一次 barrie 中的方法
                try {
                    System.out.println(Thread.currentThread().getName() + " ===> 线程开始执行");
                    barrie.await();
                    System.out.println(Thread.currentThread().getName() + " ===> 线程完毕");
                } catch (InterruptedException | BrokenBarrierException e) {
                    throw new RuntimeException(e);
                }
            }, i + "").start();
//            System.out.println("============main=========》》 " + i);
        }
    }
}
