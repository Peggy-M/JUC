# JUC
马士兵多线程与高并发编程
## 第一天
### 加锁与不加锁造成的数据丢失问题
~~~ java

/**
 * 加锁与不加锁造成的数据丢失
 * @author peggy
 * @date 2023-03-09 13:31
 */
public class SynchroizedeDome01 {
    public synchronized void m1() {
        System.out.println(Thread.currentThread().getName() + "开始运行");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(Thread.currentThread().getName() + "运行结束");

    }

    public synchronized void m2() {
        System.out.println(Thread.currentThread().getName() + "开始运行");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(Thread.currentThread().getName() + "运行结束");
    }

    public static void main(String[] args) {
        SynchroizedeDome01 t = new SynchroizedeDome01();
        new Thread(t::m1, "m1").start();
        new Thread(t::m2, "m2").start();
    }
}
~~~

### 在方法的执行过程中出现异常提前释放锁资源
~~~ java


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

~~~
### 可重新入锁 synchronized 测试
~~~ java

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

~~~

### 对于基本数据类型 String 类型的常量是不能做为锁对象使用的
~~~ java

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
~~~ 

## 第二天
### volatile 线程的可见性问题
~~~ java
/**
 * volatile 线程的可见性
 * @author peggy
 * @date 2023-03-10 9:38
 */

public class VolatileThread {
    /*volatile*/ boolean runing = true;
    void m() {
        System.out.println("线程" + Thread.currentThread().getName() + "开始运行");
        while (runing) {
        }
        System.out.println("线程" + Thread.currentThread().getName() + "运行结束");
    }
    public static void main(String[] args) {
        VolatileThread vo = new VolatileThread();
        new Thread(vo::m, "m").start();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        vo.runing = false;
    }
}
~~~

### volatile 无法保证其原子性
~~~ java
/**
 * Volatile 无法保证其原子性
 * @author peggy
 * @date 2023-03-10 20:30
 */
public class VolatileNoSort {

    private volatile int num = 0;

    //如果通过 synchronized 加锁,那么是可以保证其原子性的
    public /*synchronized*/ void add() {
        for (int i = 0; i < 10000; i++) num++;
    }

    public static void main(String[] args) {

        ArrayList<Thread> threads = new ArrayList<>();

        VolatileNoSort vo = new VolatileNoSort();

        for (int i = 0; i < 10; i++) {
            threads.add(new Thread(vo::add));
        }

        threads.forEach(Thread::start);

        threads.forEach(o -> {
            try {
                o.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        System.out.println(vo.num);
    }
}
~~~

### 锁细化 与 锁粗化 的使用场景
~~~ java
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
~~~

### 锁对象发生改变引起的锁失效
~~~ java
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
~~~

### 单例模式
#### 懒汉式单利
~~~ java
/**
 * 懒汉 Synchronized 模式
 * @author peggy
 * @date 2023-03-10 15:18
 */
public class HungerSingletonSynchronized {

    private static HungerSingletonSynchronized hungerSingletonSynchronized = null;

    private HungerSingletonSynchronized() {

    }

    //如果不存在才进行对象的创建
    public static HungerSingletonSynchronized getHungerSingleton() {
        if (hungerSingletonSynchronized == null) {
            synchronized (hungerSingletonSynchronized){
                //这里存在线程的安全问题,假设线程 A 执行到此位置，这个时候线程 B 在 synchronized 的位置等待
                //这个时候线程 A 已经处理完毕创建了一个当前的对象，并且释放了锁
                //线程 B 获取到了锁,但是由于现象 B 已经经历过了当前的对象的非空判断,这个时候线程 B 继续执行就会创建一个新的对象
                //在这种的情况下就无法保证当前的对象是单例的
                try {
                    Thread.sleep(2000);
                    System.out.println("当前的线程"+Thread.currentThread().getName()+"开始执行");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                hungerSingletonSynchronized = new HungerSingletonSynchronized();
            }
        }
        return hungerSingletonSynchronized;
    }
}
~~~

#### 饿汉式单利
~~~ java
/**
 * 饿汉模式(线程不安全)
 *
 * @author peggy
 * @date 2023-03-10 15:15
 */
public class HungerSingleton {
    private static HungerSingleton hungerSingleton = null;

    private HungerSingleton() {

    }

    //如果不存在才进行对象的创建
    public static HungerSingleton getHungerSingleton() {
        if (hungerSingleton == null) {
            //添加一个睡眠的时间，更高触发在多线的情况下存在的线程安全的问题
            try {
                Thread.sleep(2000);
                System.out.println("当前的线程"+Thread.currentThread().getName()+"开始执行");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            hungerSingleton = new HungerSingleton();
        }
        return hungerSingleton;
    }
}
~~~
#### 单利模式下的双重检验锁中关于 volatile 的使用
~~~ java
/**
 * 单利模式下的双重检验锁中关于 volatile 的使用
 *
 * @author peggy
 * @date 2023-03-10 15:14
 */
public class SingletonDoubleCheckout {

    private static volatile SingletonDoubleCheckout singletonDoubleCheckout = null;

    private SingletonDoubleCheckout() {

    }

    //如果不存在才进行对象的创建
    public static SingletonDoubleCheckout getHungerSingleton() {
        if (singletonDoubleCheckout == null) {
            synchronized (singletonDoubleCheckout) {
                //这里多出来一部关于当前的对象的再次判断
                if (singletonDoubleCheckout == null) {
                    try {
                        Thread.sleep(2000);
                        System.out.println("当前的线程" + Thread.currentThread().getName() + "开始执行");
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                //同时这里还有一个质量重排序的问题,所以对于 SingletonDoubleCheckout 对象需要添加一个 volatile 的关键字

                /**
                 *      Object obj = new Object();
                 *
                 *      0    new             #2 <java/lang/Object>
                 *      3    dup
                 *      4    invokespecial   #1 <java/lang/Object.<init> : ()V>
                 *      7    astore_1
                 *      8    return
                 *
                 */
                //为了防止出现指令重新排序导致的线程安全问题，这里对象需要用 volatile 进行修饰 由于读写屏障的存在限制了重排序
                singletonDoubleCheckout = new SingletonDoubleCheckout();
            }
        }
        return singletonDoubleCheckout;
    }
}
~~~
### 关于 Atomic 内部的实现的原理
~~~ java
/**
 * 关于 Atomic 内部的实现的原理
 *  Atomic 的内部调用的
 *  this.compareAndSwapInt(var1, var2, var5, var5 + var4)
 *  cas(V,Expected,NewValue)
 *  -CPU 原语支持,一旦开始就无法打断
 * @author peggy
 * @date 2023-03-10 21:48
 */
public class AtomicIntergerThread {
    AtomicInteger num = new AtomicInteger();

    public void addNum() {
        for (int j = 0; j < 10000; j++) {
            num.incrementAndGet();
        }
    }

    public static void main(String[] args) {

        ArrayList<Thread> threads = new ArrayList<>();

        AtomicIntergerThread at = new AtomicIntergerThread();

        for (int i = 0; i < 10; i++) {
            threads.add(new Thread(at::addNum));
        }

        threads.forEach(o -> o.start());
        threads.forEach(o -> {
            try {
                o.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        System.out.println(at.num);
    }

}
~~~
