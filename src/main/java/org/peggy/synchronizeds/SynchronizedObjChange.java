package org.peggy.synchronizeds;

/**
 * 锁对象发生改变引起的锁失效
 *
 * 可以通过 final 关键字进行处理,只允许该变量修改一次
 *
 * @author peggy
 * @date 2023-03-10 15:23
 */
public class SynchronizedObjChange {
    private /*final*/ Object obj = new Object();

    public void getObj() {
        synchronized (obj) {
            System.out.println("当前的线程:" + Thread.currentThread().getName() + "开始");
            //这里改变了对象，当前的锁失效
            obj = new Object();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("当前的线程:" + Thread.currentThread().getName() + "结束");
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 1000; i++) {
            new Thread(() -> new SynchronizedObjChange().getObj(), i + "").start();
        }
    }
}
