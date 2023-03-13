package org.peggy.synchronizeds;

/**
 * 锁细化 与 锁粗化 的使用场景
 *
 * 比如在某个场景下,一个线程需要对一个对象中的方法进行大量的调用
 * 那么及时该线程获取了锁,但在执行万当下加锁的方法后,又要立刻执行该对象的其他方法
 * 此时,该线程就需要先释放了当前锁,然后重新获取新锁,才可以执行当前的方法
 * 这样及时对于单个线程来说,频繁进行加锁与释放锁,对 CPU 资源的占用的开销是非常高
 * 
 * 如果说,我们使用的是粗粒度的锁
 * 当一个线程获取锁后,在调用该对象的其他的方法的时候,就无须再进行释放与获取
 * 减少了资源的开销
 *
 * @author peggy
 * @date 2023-03-10 15:24
 */
public class SynchronizedRange {
    private String name;
    private Integer age;
    //在每一个方法上都添加一个锁,锁细化
    public synchronized String getName() {
        return name;
    }

    public synchronized void setName(String name) {
        this.name = name;
    }

    public synchronized Integer getAge() {
        return age;
    }

    public synchronized void setAge(Integer age) {
        this.age = age;
    }

}
