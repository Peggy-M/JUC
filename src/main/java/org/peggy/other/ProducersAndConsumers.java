package org.peggy.other;

import java.util.ArrayList;
import java.util.List;

/**
 * 写一个固定容量的同步的容器
 * 用于 put 与 get 方法,以及 getCount 方法
 * 能够支持 2 个生产者与 10 个消费者的阻塞调用
 *
 * @author peggy
 * @date 2023-03-15 15:22
 */
public class ProducersAndConsumers {
    static List<Object> tankage = new ArrayList<>();

    public synchronized void put(Object obj) {
        tankage.add(obj);
    }

    public synchronized void get() {
        tankage.remove(1);
    }

    public synchronized int getCount() {
        return tankage.size();
    }

    public static void main(String[] args) {

        ProducersAndConsumers p = new ProducersAndConsumers();

        Thread[] producer = new Thread[2];
        Thread[] cousumer = new Thread[10];

        for (int i = 0; i < producer.length; i++) {
            producer[i] = new Thread(() -> {
                //如果有剩余消费者消费
                if (p.getCount() > 0) {
                    p.get();
                    System.out.println("消费者" + Thread.currentThread().getName() + "消费成功==> " + p.getCount());
                }
            }, "消费者-" + i);
        }

        for (int i = 0; i < cousumer.length; i++) {
            cousumer[i] = new Thread(() -> {
                //如果有剩余消费者消费
                if (p.getCount() <= 0) {
                    p.put(new Object());
                    System.out.println("生产者" + Thread.currentThread().getName() + "生产成功==> " + p.getCount());
                }
            }, "生产者-" + i);
        }

        for (Thread thread : producer) {
            thread.start();
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        for (Thread thread : cousumer) {
            thread.start();
        }

    }
}
