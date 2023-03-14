package org.peggy.other;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * ReadWriteLock 读写锁
 * 用于读（共享锁）多写（排它锁）少的情况
 * @author peggy
 * @date 2023-03-14 14:54
 */
public class T07_ReadWriteLock {
    static ReentrantLock lock = new ReentrantLock();
    static ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    static Lock redaLock = readWriteLock.readLock();
    static Lock writeLock = readWriteLock.writeLock();

    public static void read(Lock lock) {
        lock.lock();
        try {
            TimeUnit.SECONDS.sleep(1);
            System.out.println("reda...over");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }

    }

    public static void write(Lock lock) {
        lock.lock();
        try {
            TimeUnit.SECONDS.sleep(1);
            System.out.println("write...over");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
//        无论执行那个方法都会处于阻塞的状态
//        Runnable reLock = () -> read(lock);

//        对于读的状态是一个 共享锁 允许多线程并发执行,
        Runnable reLock = () -> read(redaLock);

//        Runnable wrLock = () -> write(lock);
//        对于写状态而言,此时就是一个 排它锁 单位时间内只允许一个线程访问
        Runnable wrLock = () -> write(writeLock);

        for (int i = 0; i < 10; i++) new Thread(reLock).start();
        for (int i = 0; i < 2; i++) new Thread(wrLock).start();
    }
}
