package org.peggy.other;

import java.util.concurrent.TimeUnit;

/**
 * 本地多线程
 * @author peggy
 * @date 2023-03-16 11:12
 */
public class T13_ThreadLock {
    static ThreadLocal<User> t1  = new ThreadLocal<>();

    public static void main(String[] args) {
        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            final User user = t1.get();
            System.out.println(user);
        }).start();

        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            t1.set(new User("卡卡罗特", 18));


            /**
             *
             * ThreadLocal values pertaining to this thread. This map is maintained
             * by the ThreadLocal class.
             *
             * ThreadLocal.ThreadLocalMap threadLocals = null;
             *
             *     public void set(T value) {
             *         Thread t = Thread.currentThread(); //获取当前的线程对象
             *         ThreadLocalMap map = getMap(t); //获取当前线程对象的中 ThreadLocalMap 对象
             *         if (map != null) {
             *             map.set(this, value); //将当前线程对象做为 key
             *         } else {
             *             createMap(t, value);
             *         }
             *     }
             */
        }).start();
    }

}
