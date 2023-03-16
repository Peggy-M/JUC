package org.peggy.other;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * CountDownLath 方法测试
 * @author peggy
 * @date 2023-03-15 14:38
 */
public class T11_CountDownLath {
    volatile List lists = new ArrayList<>();

    public void add(Object o) {
        lists.add(o);
    }

    public int size() {
        return lists.size();
    }

    public static void main(String[] args) {
        T11_CountDownLath countDownLath = new T11_CountDownLath();

        CountDownLatch latch = new CountDownLatch(1);

        new Thread(() -> {
            System.out.println("线程 T2 启动");
            if (countDownLath.size() != 5) {
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            System.out.println("线程 T2 结束");
        },"T2").start();

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        new Thread(() -> {
            System.out.println("线程 T1 启动");
            for (int i = 0; i < 10; i++) {
                countDownLath.add(new Object());
                System.out.println("add--->" + i);
                if (countDownLath.size() == 5) {
                    latch.countDown();
                }
                /*
                * 这里存在的问题就是,虽然当 i=5 的时候 T1 线程,执行了 countDown 方法
                * 但是由于 T2 还没来的及执行打印方法
                * T1 已经开始执行并打印
                * 当我们在这里执行休眠方法的时候,为线程 T2 提供了打印的时间
                * */
                /*try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }*/
            }
        },"T1").start();

    }
}
