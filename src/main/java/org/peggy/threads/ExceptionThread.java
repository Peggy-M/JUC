package org.peggy.threads;

/**
 * 在方法执行的过程中如果出现了异常的情况会提取的释放锁资源
 * @author peggy
 * @date 2023-03-09 16:03
 */
public class ExceptionThread {

    public synchronized int getAverage(int number, int div) {
        System.out.println(Thread.currentThread().getName() + "线程开始处理数据");
        int r = 0;
        /*try {*/
            r = number / div;
       /* } catch (Exception e) {
            System.out.println("已解决" + Thread.currentThread().getName() + "线程问题");
        }*/
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(Thread.currentThread().getName() + "线程数据处理完毕" + "====>" + r);
        return r;
    }

    public static void main(String[] args) {
        ExceptionThread exceptionThread = new ExceptionThread();
        Thread t1 = new Thread(() -> {
            exceptionThread.getAverage(100, 0);
        }, "t1");
        Thread t2 = new Thread(() -> {
            exceptionThread.getAverage(50, 2);
        }, "t2");

        t1.start();
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        t2.start();
    }
}
