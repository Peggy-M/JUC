package org.peggy.vessel;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * Queue 队列进行多窗口并发售票
 * 通过队列的头节点判断是否还存在空余
 * @author peggy
 * @date 2023-03-17 15:07
 */
public class T04_TicketSeller {
    static Queue<String> tickets = new ConcurrentLinkedQueue<>();

    static {
        for (int i = 0; i < 1000; i++) {
            tickets.add("票编号:" + i);
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            new Thread(()->{

                while (tickets.size() > 0) {
                    //检索头部的元素,并返回
                    /*
                     *  由于这个方法是没有加锁,所以执行的效率要比 tickets.size() 要高
                     *  而且内部是基础与 Collection 的内部做了线程的同步 Queue<E> extends Collection<E>
                     *  所以 tickets.poll() 是线程安全的
                     */
                    final String poll = tickets.poll();
                    if (poll == null) break;

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    System.out.println("销售了--->" + poll);
                }

            }).start();
        }
    }
}
