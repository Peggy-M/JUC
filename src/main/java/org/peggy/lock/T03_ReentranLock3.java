package org.peggy.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 可以被打断的锁,可以在等待的过程中对线程的  interrupt() 方法做出响应
 *
 * @author peggy
 * @date 2023-03-13 15:46
 */
public class T03_ReentranLock3 {

    Lock lock = new ReentrantLock();

    void m1() {
        try {
            lock.lock();
            System.out.println("m1 start run ... ");
            Thread.sleep(Integer.MAX_VALUE);
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
            lock.lockInterruptibly();
            System.out.println("m2开始执行");
        } catch (InterruptedException e) {
            System.out.println("强行终止当前 m2 线程的等待。。。。");
//            throw new RuntimeException(e);
        } finally {
            if (locked) {
                //锁释放
                lock.unlock();
            }
        }
    }

    public static void main(String[] args) {
        T03_ReentranLock3 t1 = new T03_ReentranLock3();
        Thread m1 = new Thread(t1::m1, "A");
        m1.start();
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Thread m2 = new Thread(t1::m2, "B");
        m2.start();
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //打断线程当前 m2 线程
        m2.interrupt();
    }

}
