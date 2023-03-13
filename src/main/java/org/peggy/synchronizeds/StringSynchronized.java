package org.peggy.synchronizeds;

/**
 * 对于基本数据类型 String 类型的常量是不能做为锁对象使用的
 * 这样很可能对造成其他加载的线程出现被锁占用的情况
 * 因为 String 类型的变量会将对象存储在缓存池中
 * 而对于基本数据类型的包装类,一部分数据也做了缓存的处理
 * @author peggy
 * @date 2023-03-10 19:50
 */
public class StringSynchronized {
//    String s1 = "Hello";
//    String s2 = "Hello";

    Integer s1 = 1;
    Integer s2 = 1;

    public void getValue1() {
        synchronized (s1) {
            System.out.println("当前的线程:"+Thread.currentThread().getName());
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("s1==>"+s1);
        }
    }

    public void getValue2(){
        synchronized(s2){
            System.out.println("当前的线程:"+Thread.currentThread().getName());
            System.out.println("s2==>"+s2);
        }
    }

    public static void main(String[] args) {
        StringSynchronized stringSynchronized = new StringSynchronized();
        new Thread(stringSynchronized::getValue1,"t1").start();
        new Thread(stringSynchronized::getValue2,"t2").start();
    }
}
