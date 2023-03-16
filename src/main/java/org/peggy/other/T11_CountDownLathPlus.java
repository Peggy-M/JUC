package org.peggy.other;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * CountDownLath 方法测试
 * 为了解决上述出现的问题，需要处理就是添加在原来 T1 线程中的方法中添加一个新的门闩
 * @author peggy
 * @date 2023-03-15 14:38
 */
public class T11_CountDownLathPlus {
    volatile List lists = new ArrayList<>();

    public void add(Object o) {
        lists.add(o);
    }

    public int size() {
        return lists.size();
    }

    public static void main(String[] args) {
        T11_CountDownLathPlus countDownLath = new T11_CountDownLathPlus();

        CountDownLatch latch1 = new CountDownLatch(1);
        CountDownLatch latch2 = new CountDownLatch(1);

        new Thread(() -> {
            System.out.println("线程 T2 启动");
            if (countDownLath.size() != 5) {
                try {
                    latch1.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            System.out.println("线程 T2 结束");
            latch2.countDown();
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
                    latch1.countDown();
                    try {
                        latch2.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        },"T1").start();

    }
}
