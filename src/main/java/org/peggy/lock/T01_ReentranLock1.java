package org.peggy.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 通过 Lock 上锁解锁
 *
 * @author peggy
 * @date 2023-03-13 14:54
 */
public class T01_ReentranLock1 {
    Lock lock = new ReentrantLock();

    void m1() {
        lock.lock();
        try {
            for (int i = 0; i < 10; i++) {
                System.out.println(Thread.currentThread().getName() + "==>" + i);
                TimeUnit.SECONDS.sleep(1);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //锁释放
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        T01_ReentranLock1 t1 = new T01_ReentranLock1();
        new Thread(t1::m1, "A").start();
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        new Thread(t1::m1, "B").start();
    }
}
