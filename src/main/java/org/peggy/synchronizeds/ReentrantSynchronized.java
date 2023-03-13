package org.peggy.synchronizeds;

/**
 * 可重新入锁 synchronized 测试
 * 如果说父类中的一个方法加锁，而子类对父类的方法进行了重写
 * 而在调用的过程中的调用的是 supper 父类下的方法，如果不是可重入锁，那么在执行子类的时候就会进入死锁的状态
 *
 * @author peggy
 * @date 2023-03-09 15:25
 */

public class ReentrantSynchronized extends FatherSynchronized {

    /**
     * 通过这种基础实现其实就已经证明了 synchronized 锁是可重入的锁.
     * 否则就会出现由于先调用了子类的方法而子类优先获得了锁,当在子类中调用父类的时候,
     * 由于父类也需要获得当前对象的锁,但由于子类的方法还没有执行完毕,
     * 因此子类方法是无法释放锁资源的,父类方法就无法获取当前锁资源,导致父类的方法无法执行,
     * 而子类方法始终无法执行完毕，处于一个僵持的死锁状态
     */
    @Override
    public synchronized void setAge() {
        super.setAge();
        System.out.println("获取年龄为:" + this.age);
    }

    public synchronized void t1() {
        System.out.println("方法t1开始执行");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        t2();
        System.out.println("方法t1执行完毕");
    }

    public synchronized void t2() {
        System.out.println("方法t2开始执行");
    }
    public static void main(String[] args) {
        ReentrantSynchronized r = new ReentrantSynchronized();
        r.t1();
        r.setAge();
    }
}
