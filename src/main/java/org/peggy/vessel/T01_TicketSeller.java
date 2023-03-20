package org.peggy.vessel;

import java.util.ArrayList;
import java.util.List;

/**
 * ArrayList 模拟多窗口抢票引发的线程安全问题
 * @author peggy
 * @date 2023-03-17 14:33
 */
public class T01_TicketSeller {
    static List<String> tickets = new ArrayList<>();

    static {
        for (int i = 0; i < 10000; i++) {
            tickets.add("票编号: " + i);
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                //容器中的是否还有剩余
                while (tickets.size() > 0) { //当执行到这里的时候就会出现问题,在多线程环境下 这里就会出现问题
                    //每次从中取出一个
                    System.out.println("销售了----> "+tickets.remove(0));
                }
            }).start();
        }
    }
}
