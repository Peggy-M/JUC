package org.peggy;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author peggy
 * @date 2023-03-14 17:16
 */
public class test {
    
    public static void main(String[] args) {
        final ReentrantLock lock = new ReentrantLock();
        lock.lock();
        System.out.println("卡卡罗特");
        lock.unlock();
        AtomicInteger[] array=new AtomicInteger[10];
        int i = array[1].get();
        array[1].addAndGet(1);
        System.out.println(i);
    }
}
