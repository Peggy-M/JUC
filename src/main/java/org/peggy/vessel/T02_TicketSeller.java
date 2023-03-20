package org.peggy.vessel;

import java.util.Vector;

/**
 * Vector 抢票的非原子性操作引发的问题
 * @author peggy
 * @date 2023-03-17 14:42
 */
public class T02_TicketSeller {
    static Vector<String> tickets = new Vector<>();

    static {
        for (int i = 0; i < 10000; i++) {
            tickets.add("票的编号:" + i);
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                while (tickets.size()>0){
                    try {
                        /**
                         * 到最后一张票了,先有一个线程抢到了执行权执行了 size 方法,进入循环体中
                         * 但是由于这个休眠的期间进入循环体中的这个线程提前释放了锁,但 remove 方法还没有来的及执行
                         * 其他线程已经通过 size 线程进行了判断,并且条件成立
                         * 这个时候,就会出现多线线程在只有一张余票的情况下就已经进入到了循环体内部
                         * 此时,问题就来了。
                         * 只有一张票,多个线程中只有一个线程可以有效的执行 remove 方法, 只有执行完毕以后
                         * 其他的线程再去执行 remove 方法的时候,此时容器中的值已经为空了
                         */
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    System.out.println("销售了--->"+tickets.remove(0));
                }
            }).start();
        }
    }
}
