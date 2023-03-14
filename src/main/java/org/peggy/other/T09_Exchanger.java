package org.peggy.other;

import java.util.concurrent.Exchanger;

/**
 *  Exchanger 线程的交换
 * @author peggy
 * @date 2023-03-14 21:16
 */
public class T09_Exchanger {
    static Exchanger<String> exchanger = new Exchanger<>();

    public static void main(String[] args) {
        new Thread(()->{
            String str="t1";
            try {
                final String s = exchanger.exchange(str);
                System.out.println("线程==>"+Thread.currentThread().getName()+"===>str==>"+s);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        },"T1").start();

        new Thread(()->{
            String str="t2";
            try {
                final String s = exchanger.exchange(str);
                System.out.println("线程==>"+Thread.currentThread().getName()+"===>str==>"+s);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        },"T2").start();
    }
}
