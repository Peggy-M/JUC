package org.peggy;

/**
 * 为加锁的方法与加锁的方法可以同时的访问
 * @author peggy
 * @date 2023-03-09 14:38
 */
public class AccountThread {

    Integer accout = 100;

    public synchronized void setAccout(Integer acc) {
        try {
            Thread.sleep(1000);
            this.accout -= acc;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public /*synchronized*/ void getAccount() {
        System.out.println("获取的当前用户的金额为: " + accout);
    }

    public static void main(String[] args) {
        AccountThread accountThread = new AccountThread();
        new Thread(() -> {
            accountThread.setAccout(100);
        }).start();
        new Thread(accountThread::getAccount).start();
    }
}
