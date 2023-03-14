package org.peggy.test;

/**
 * 线程 Join 方法测试
 *
 * @author peggy
 * @date 2023-03-13 13:47
 */
public class JoinThreadTest {
    public void add() {
        System.out.println("当前的线程:" + Thread.currentThread().getName() + "开始执行");
        try {
            for (int i = 0; i < 100; i++) {
                Thread.sleep(500);
                System.out.println(Thread.currentThread().getName() + "==>" + i);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("当前的线程" + Thread.currentThread().getName() + "执行完毕........");
    }

    public static void main(String[] args) {
        JoinThreadTest joinThreadTest = new JoinThreadTest();

        Thread A = new Thread(joinThreadTest::add, "A");
        Thread B = new Thread(joinThreadTest::add, "B");
        A.start();
        try {
            //加入 join 的作用是防止, main 线程在其他线程之前提前结束,从而导致程序提前的终止
            B.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        B.start();
    }
}
