package org.peggy.volatiles;

import java.util.concurrent.TimeUnit;

/**
 * volatile 线程的可见性
 * @author peggy
 * @date 2023-03-10 9:38
 */

public class VolatileThread {
    /*volatile*/ boolean runing = true;
    void m() {
        System.out.println("线程" + Thread.currentThread().getName() + "开始运行");
        while (runing) {
        }
        System.out.println("线程" + Thread.currentThread().getName() + "运行结束");
    }
    public static void main(String[] args) {
        VolatileThread vo = new VolatileThread();
        new Thread(vo::m, "m").start();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        vo.runing = false;
    }
}
