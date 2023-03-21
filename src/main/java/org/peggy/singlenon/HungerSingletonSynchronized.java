package org.peggy.singlenon;

/**
 * 懒汉 Synchronized 模式
 * @author peggy
 * @date 2023-03-10 15:18
 */
public class HungerSingletonSynchronized {

    private static HungerSingletonSynchronized hungerSingletonSynchronized = null;

    private HungerSingletonSynchronized() {

    }

    //如果不存在才进行对象的创建
    public static HungerSingletonSynchronized getHungerSingleton() {
        if (hungerSingletonSynchronized == null) {
            synchronized (HungerSingletonSynchronized.class){
                //这里存在线程的安全问题,假设线程 A 执行到此位置，这个时候线程 B 在 synchronized 的位置等待
                //这个时候线程 A 已经处理完毕创建了一个当前的对象，并且释放了锁
                //线程 B 获取到了锁,但是由于现象 B 已经经历过了当前的对象的非空判断,这个时候线程 B 继续执行就会创建一个新的对象
                //在这种的情况下就无法保证当前的对象是单例的
                try {
                    Thread.sleep(2000);
                    System.out.println("当前的线程"+Thread.currentThread().getName()+"开始执行");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                hungerSingletonSynchronized = new HungerSingletonSynchronized();
            }
        }
        return hungerSingletonSynchronized;
    }
}
