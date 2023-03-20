package org.peggy.vessel;

import java.util.Vector;

/**
 * Vector 通过二次判断解决抢票引发的线程安全问题
 *
 * @author peggy
 * @date 2023-03-17 14:42
 */
public class T03_TicketSeller {
    static Vector<String> tickets = new Vector<>();

    static {
        for (int i = 0; i < 1000; i++) {
            tickets.add("票的编号:" + i);
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                while (tickets.size() > 0) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    if (tickets.size() <= 0) break;
                    System.out.println("销售了--->" + tickets.remove(0));
                }
            }).start();
        }
    }
}
