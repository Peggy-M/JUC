package org.peggy.synchronizeds;

/**
 * 加锁与不加锁造成的数据丢失
 * @author peggy
 * @date 2023-03-09 13:31
 */
public class SynchroizedeDome01 {
    public synchronized void m1() {
        System.out.println(Thread.currentThread().getName() + "开始运行");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(Thread.currentThread().getName() + "运行结束");

    }

    public synchronized void m2() {
        System.out.println(Thread.currentThread().getName() + "开始运行");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(Thread.currentThread().getName() + "运行结束");
    }

    public static void main(String[] args) {
        SynchroizedeDome01 t = new SynchroizedeDome01();
        new Thread(t::m1, "m1").start();
        new Thread(t::m2, "m2").start();
    }
}
