package org.peggy.singlenon;

/**
 * 饿汉模式(线程不安全)
 *
 * @author peggy
 * @date 2023-03-10 15:15
 */
public class HungerSingleton {
    private static HungerSingleton hungerSingleton = null;

    private HungerSingleton() {

    }

    //如果不存在才进行对象的创建
    public static HungerSingleton getHungerSingleton() {
        if (hungerSingleton == null) {
            //添加一个睡眠的时间，更高触发在多线的情况下存在的线程安全的问题
            try {
                Thread.sleep(2000);
                System.out.println("当前的线程"+Thread.currentThread().getName()+"开始执行");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            hungerSingleton = new HungerSingleton();
        }
        return hungerSingleton;
    }
}
