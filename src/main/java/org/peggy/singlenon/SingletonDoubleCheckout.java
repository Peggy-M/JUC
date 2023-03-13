package org.peggy.singlenon;

/**
 * 单利模式下的双重检验锁中关于 volatile 的使用
 *
 * @author peggy
 * @date 2023-03-10 15:14
 */
public class SingletonDoubleCheckout {

    private static volatile SingletonDoubleCheckout singletonDoubleCheckout = null;

    private SingletonDoubleCheckout() {

    }

    //如果不存在才进行对象的创建
    public static SingletonDoubleCheckout getHungerSingleton() {
        if (singletonDoubleCheckout == null) {
            synchronized (singletonDoubleCheckout) {
                //这里多出来一部关于当前的对象的再次判断
                if (singletonDoubleCheckout == null) {
                    try {
                        Thread.sleep(2000);
                        System.out.println("当前的线程" + Thread.currentThread().getName() + "开始执行");
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                //同时这里还有一个质量重排序的问题,所以对于 SingletonDoubleCheckout 对象需要添加一个 volatile 的关键字

                /**
                 *      Object obj = new Object();
                 *
                 *      0    new             #2 <java/lang/Object>
                 *      3    dup
                 *      4    invokespecial   #1 <java/lang/Object.<init> : ()V>
                 *      7    astore_1
                 *      8    return
                 *
                 */
                //为了防止出现指令重新排序导致的线程安全问题，这里对象需要用 volatile 进行修饰 由于读写屏障的存在限制了重排序
                singletonDoubleCheckout = new SingletonDoubleCheckout();
            }
        }
        return singletonDoubleCheckout;
    }
}
