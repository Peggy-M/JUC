package org.peggy.test;


public class test {
    Thread thread1=new Thread(()->{
        System.out.println("这是一个线程");
    });


    class MyRun implements Runnable{
        @Override
        public void run() {
            System.out.println("这是一个线程");
        }
    }

    Thread thread2=new Thread(new MyRun());
}
