package org.peggy.other;

import java.util.concurrent.CountDownLatch;

/**
 * CountDownLatch 阻塞倒计时的使用
 * 应用场景主要是阻塞当前线程,让后台的线程执行完毕后继续执行
 * CountDownLatch(int count)
 * count 倒计时计数的结束
 *
 * @author peggy
 * @date 2023-03-13 16:13
 */
public class T04_CountDownLatch {
    static Thread[] thread = new Thread[100];
    static CountDownLatch countDownLatch = new CountDownLatch(thread.length);

    public static void main(String[] args) {
        for (int i = 0; i < thread.length; i++) {
            thread[i] = new Thread(() -> {
                for (int k = 0; k < 1000; k++) {
                    System.out.println(Thread.currentThread().getName() + "=====>" + k);
                }
                countDownLatch.countDown();
                System.out.println("线程" + Thread.currentThread().getName() + "执行完毕");
            }, i + "");
        }
        for (Thread t : thread) t.start();

        try {
            //这个的作用相当于 join ,这里阻塞了当前的 main 线程，直到上面线程执行完毕后才释放
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("main 线程开始执行完毕....");
    }

}
