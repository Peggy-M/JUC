package org.peggy.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 尝试等待时间,防止阻塞
 * lock.tryLock(10, TimeUnit.SECONDS);
 * 设置等待尝试获取锁
 * - 如果在等待的时间内获取锁了则返回 true
 * - 如果没有获取锁则返回 false
 * - 可以根据返回的具体结果在 finally 代码块中进行处理其他的方法
 *
 * @author peggy
 * @date 2023-03-13 15:22
 */
public class T02_ReentranLock2 {
    Lock lock = new ReentrantLock();

    void m1() {
        try {
            lock.lock();
            for (int i = 0; i < 5; i++) {
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

    void m2() {
        boolean locked = false;
        try {
            locked = lock.tryLock(10, TimeUnit.SECONDS);
            System.out.println("m2==" + locked);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (locked) {
                //锁释放
                lock.unlock();
            }
        }
    }

    public static void main(String[] args) {
        T02_ReentranLock2 t1 = new T02_ReentranLock2();
        new Thread(t1::m1, "A").start();
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        new Thread(t1::m2, "B").start();
    }

}
