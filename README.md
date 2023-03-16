# JUC

目录
----------------------------------------------------------------------------------------------------------------------------------------------------------------------
**Day01**
1. 加锁与不加锁造成的数据丢失问题
2. 在方法的执行过程中出现异常提前释放锁资源
3. 可重新入锁 synchronized 测试
4. 对于基本数据类型 String 类型的常量是不能做为锁对象使用

**Day02**
1. volatile 线程的可见性问题
2. 在方法的执行过程中出现异常提前释放锁资源
3. 可重新入锁 synchronized 测试
4. 锁细化 与 锁粗化 的使用场景
5. 锁对象发生改变引起的锁失效
6. 单例模式
    - 懒汉式单利
    - 饿汉式单利
7. 单利模式下的双重检验锁中关于 volatile 的使用
8. 关于 Atomic 内部的实现的原理

**Day03**
1. 关于 Atome 与 Sychronizeds 与 Adder 之间的性能测试
2. 通过 Lock 上锁解锁
3. 尝试等待时间,防止阻塞 ReentrantLock 的 lock.tryLock(10, TimeUnit.SECONDS) 方法
4. 可以被打断的锁,可以在等待的过程中对线程的 interrupt() 方法做出响应
5. CountDownLatch 阻塞倒计时的使用
6. CyclicBarrier 的阻塞等待执行
7. Phaser 线程的阶段控制
8. ReadWriteLock 读写锁
9. 信号量 Semaphore 同时允许有多个线程在运行
10. Exchanger 线程中的变量交换

**Day05**
1. 固定容量的同步的容器
1. CountDownLath 方法测试
1. CountDownLath 方法测试第二版
1. VarHandle 操作内存中原子性的操作
1. 本地多线程
1. 软引用
1. 强引用
1. 虚引用
1. 弱引用
----------------------------------------------------------------------------------------------------------------------------------------------------------------------



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
## 第三天
### 关于 Atome 与 Sychronizeds 与 Adder 之间的性能测试
~~~ java
/**
 * 关于 Atome 与 Sychronizeds 与 Adder 之间的性能测试 (这个测试类有点问题)
 * 通过下面的模拟可以看出,在高并发的环境下
 * LongAdder 的执行效率要比 AtomicLong 的执行效率要高
 * 因为 LongAdder 的底层使用的一种分段 cas 锁
 *
 * @author peggy
 * @date 2023-03-13 13:37
 */
public class AtomeVsSyncVsLongAdder {

    static long count1 = 0L;
    static AtomicLong count2 = new AtomicLong(0L);
    static LongAdder longAdder = new LongAdder();


    //创建线程并放置到数组中
    public static void main(String[] args) {
        Object lock = new Object();

        //通过 synchronized 加锁占用的时间
        Thread[] threads = new Thread[1000];
        for (int i = 0; i < threads.length; i++) {
            threads[i] =
                    new Thread(() -> {
                        for (int k = 0; k < 100000; k++) {
                            synchronized (lock) {
                                count1++;
                            }
                        }
                    });
        }
        long start = System.currentTimeMillis();
        for (Thread thread : threads) {
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("count1:" + count1 + "==>" + (end - start));

        //AtomicLong 所用总时间
        for (int i = 0; i < threads.length; i++) {
            threads[i] =
                    new Thread(() -> {
                        for (int k = 0; k < 100000; k++) count2.incrementAndGet();
                    });
        }

        start = System.currentTimeMillis();
        for (Thread t : threads) {
            t.start();
            try {
                //加入 join 的作用是防止, main 线程在其他线程之前提前结束,从而导致程序提前的终止
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        //当上面的所有的线程结束 main 线程继续
        end = System.currentTimeMillis();
        System.out.println("AtomicLong:" + count2 + "==>" + (end - start));

        //通过 longAdder 加锁占用
        //AtomicLong 所用总时间
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                for (int k = 0; k < 100000; k++) {
                    longAdder.add(1);
                }
            });
        }

        start = System.currentTimeMillis();
        for (Thread t : threads) {
            t.start();
            try {
                //加入 join 的作用是防止, main 线程在其他线程之前提前结束,从而导致程序提前的终止
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        //当上面的所有的线程结束 main 线程继续
        end = System.currentTimeMillis();
        System.out.println("longAdder:" + longAdder + "==>" + (end - start));

    }
}
~~~
~~~ java
/**
 * @author peggy
 * @date 2023-03-13 15:03
 */
public class AtomeVsSysncVsLongAdder2 {
    static long count2 = 0L;
    static AtomicLong count1 = new AtomicLong(0L);
    static LongAdder count3 = new LongAdder();

    public static void main(String[] args) throws Exception {
        Thread[] threads = new Thread[1000];

        for(int i=0; i<threads.length; i++) {
            threads[i] =
                    new Thread(()-> {
                        for(int k=0; k<100000; k++) count1.incrementAndGet();
                    });
        }

        long start = System.currentTimeMillis();

        for(Thread t : threads ) t.start();

        for (Thread t : threads) t.join();

        long end = System.currentTimeMillis();

        //TimeUnit.SECONDS.sleep(10);

        System.out.println("Atomic: " + count1.get() + " time " + (end-start));
        //-----------------------------------------------------------
        Object lock = new Object();

        for(int i=0; i<threads.length; i++) {
            threads[i] =
                    new Thread(() -> {
                        for (int k = 0; k < 100000; k++)
                            synchronized (lock) {
                                count2++;
                            }
                    });
        }

        start = System.currentTimeMillis();

        for(Thread t : threads ) t.start();

        for (Thread t : threads) t.join();

        end = System.currentTimeMillis();


        System.out.println("Sync: " + count2 + " time " + (end-start));


        //----------------------------------
        for(int i=0; i<threads.length; i++) {
            threads[i] =
                    new Thread(()-> {
                        for(int k=0; k<100000; k++) count3.increment();
                    });
        }

        start = System.currentTimeMillis();

        for(Thread t : threads ) t.start();

        for (Thread t : threads) t.join();

        end = System.currentTimeMillis();

        //TimeUnit.SECONDS.sleep(10);

        System.out.println("LongAdder: " + count1.longValue() + " time " + (end-start));

    }

    static void microSleep(int m) {
        try {
            TimeUnit.MICROSECONDS.sleep(m);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
~~~
### 通过 Lock 上锁解锁
~~~ java
/**
 * 通过 Lock 上锁解锁
 *
 * @author peggy
 * @date 2023-03-13 14:54
 */
public class T01_ReentranLock1 {
    Lock lock = new ReentrantLock();

    void m1() {
        lock.lock();
        try {
            for (int i = 0; i < 10; i++) {
                System.out.println(Thread.currentThread().getName() + "==>" + i);
                TimeUnit.SECONDS.sleep(1);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //锁释放
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        T01_ReentranLock1 t1 = new T01_ReentranLock1();
        new Thread(t1::m1, "A").start();
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        new Thread(t1::m1, "B").start();
    }
}
~~~
### 尝试等待时间,防止阻塞 ReentrantLock 的 lock.tryLock(10, TimeUnit.SECONDS) 方法
~~~ java
/**
 * 尝试等待时间,防止阻塞
 * lock.tryLock(10, TimeUnit.SECONDS);
 * 设置等待尝试获取锁
 * - 如果在等待的时间内获取锁了则返回 true
 * - 如果没有获取锁则返回 false
 * - 可以根据返回的具体结果在 finally 代码块中进行处理其他的方法
 *
 * @author peggy
 * @date 2023-03-13 15:22
 */
public class T02_ReentranLock2 {
    Lock lock = new ReentrantLock();

    void m1() {
        try {
            lock.lock();
            for (int i = 0; i < 5; i++) {
                System.out.println(Thread.currentThread().getName() + "==>" + i);
                TimeUnit.SECONDS.sleep(1);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //锁释放
            lock.unlock();
        }
    }

    void m2() {
        boolean locked = false;
        try {
            locked = lock.tryLock(10, TimeUnit.SECONDS);
            System.out.println("m2==" + locked);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (locked) {
                //锁释放
                lock.unlock();
            }
        }
    }

    public static void main(String[] args) {
        T02_ReentranLock2 t1 = new T02_ReentranLock2();
        new Thread(t1::m1, "A").start();
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        new Thread(t1::m2, "B").start();
    }

}
~~~

### 可以被打断的锁,可以在等待的过程中对线程的 interrupt() 方法做出响应

~~~ java
/**
 * 可以被打断的锁,可以在等待的过程中对线程的  interrupt() 方法做出响应
 *
 * @author peggy
 * @date 2023-03-13 15:46
 */
public class T03_ReentranLock3 {

    Lock lock = new ReentrantLock();

    void m1() {
        try {
            lock.lock();
            System.out.println("m1 start run ... ");
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //锁释放
            lock.unlock();
        }
    }

    void m2() {
        boolean locked = false;
        try {
            lock.lockInterruptibly();
            System.out.println("m2开始执行");
        } catch (InterruptedException e) {
            System.out.println("强行终止当前 m2 线程的等待。。。。");
//            throw new RuntimeException(e);
        } finally {
            if (locked) {
                //锁释放
                lock.unlock();
            }
        }
    }

    public static void main(String[] args) {
        T03_ReentranLock3 t1 = new T03_ReentranLock3();
        Thread m1 = new Thread(t1::m1, "A");
        m1.start();
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Thread m2 = new Thread(t1::m2, "B");
        m2.start();
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //打断线程当前 m2 线程
        m2.interrupt();
    }

}
~~~
### CountDownLatch 阻塞倒计时的使用
~~~ java
/**
 * CountDownLatch 阻塞倒计时的使用
 * 应用场景主要是阻塞当前线程,让后台的线程执行完毕后继续执行
 * CountDownLatch(int count)
 * count 倒计时计数的结束
 *
 * @author peggy
 * @date 2023-03-13 16:13
 */
public class T04_CountDownLatch {
    static Thread[] thread = new Thread[100];
    static CountDownLatch countDownLatch = new CountDownLatch(thread.length);

    public static void main(String[] args) {
        for (int i = 0; i < thread.length; i++) {
            thread[i] = new Thread(() -> {
                for (int k = 0; k < 1000; k++) {
                    System.out.println(Thread.currentThread().getName() + "=====>" + k);
                }
                countDownLatch.countDown();
                System.out.println("线程" + Thread.currentThread().getName() + "执行完毕");
            }, i + "");
        }
        for (Thread t : thread) t.start();

        try {
            //这个的作用相当于 join ,这里阻塞了当前的 main 线程，直到上面线程执行完毕后才释放
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("main 线程开始执行完毕....");
    }

}
~~~
### CyclicBarrier 的阻塞等待执行

~~~ java
/**
 * @author peggy
 * @date 2023-03-13 18:26
 */
public class T05_CyclicBarrier {
    public static void main(String[] args) {
        //到 20 个的时候就停止阻塞当前的线程
//                CyclicBarrier barrie = new CyclicBarrier(20);
        //当前的线程创建执行的数量达到了 20 个每次执行 barrie.await() 方法都会执行 CyclicBarrier 中的方法
        CyclicBarrier barrie = new CyclicBarrier(10, () -> {
            System.out.println("已经超出最大上限。。。。");
        });
        for (int i = 0; i < 20; i++) {
            new Thread(() -> {
                //每一个线程执行到最后调用一次 barrie 中的方法
                try {
                    System.out.println(Thread.currentThread().getName() + " ===> 线程开始执行");
                    barrie.await();
                    System.out.println(Thread.currentThread().getName() + " ===> 线程完毕");
                } catch (InterruptedException | BrokenBarrierException e) {
                    throw new RuntimeException(e);
                }
            }, i + "").start();
//            System.out.println("============main=========》》 " + i);
        }
    }
}
~~~

### Phaser 线程的阶段控制
~~~ java
/**
 * Phaser 是通过变量进行控制着 不同的阶段对当前的线程进行执行
 * stage = 1  ---> 阶段1             | A | ======> 线程 A 执行
 * stage = 2  ---> 阶段2         | A | B | ======> 线程 A B执行
 * stage = 3  ---> 阶段3     | A | B | C | ======> 线程 A B C执行
 * stage = 4  ---> 阶段4 | A | B | C | D | ======> 线程 A B C D执行
 * <p>
 * 像这种的类型就是线程以此进行执行,先异步执行,再同步执行
 * 应用场景:
 * 遗传学 可以用于过滤
 *
 * @author peggy
 * @date 2023-03-14 12:20
 */
public class T06_Phaser {
    static Random r = new Random();
    static MarriagePhaser phaser = new MarriagePhaser();

    //随机时间获取
    static void milliSleep(int milli) {
        try {
            TimeUnit.MILLISECONDS.sleep(milli);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        //设置线程等待的次数
        phaser.bulkRegister(7);

        for (int i = 0; i < 5; i++) {
            final int nameIndex = i;
            new Thread(new Person("person " + nameIndex)).start();
        }
        new Thread(new Person("新郎")).start();

        new Thread(new Person("新娘")).start();

    }


    static class MarriagePhaser extends Phaser {
        //每一个被拦截的栅栏被推倒后,就会执行的方法
        @Override
        protected boolean onAdvance(int phase, int registeredParties) {
            // phase 当前所处的阶段 registeredPatiers 当前有多少线程参与了
            switch (phase) {
                case 0:
                    System.out.println("所有人到齐了！当前的人数==>" + registeredParties);
                    return false;
                case 1:
                    System.out.println("所有人吃完了！当前的人数==>" + registeredParties);
                    return false;
                case 2:
                    System.out.println("所有人离开了！当前的人数==>" + registeredParties);
                    return false;
                case 3:
                    System.out.println("婚礼结束！当前的人数==>" + registeredParties);
                    return true;
                default:
                    return true;
            }
        }
    }


    static class Person implements Runnable {
        String name;

        public Person(String name) {
            this.name = name;
        }

        public void arrive() {
            milliSleep(r.nextInt(1000));
            System.out.printf("%s 到达现场！\n", name);
            //每一个线程执行到这个方法的时候,就会等待。当达到指定数量的时候再进行执行
            phaser.arriveAndAwaitAdvance();
        }

        public void eat() {
            milliSleep(r.nextInt(1000));
            System.out.printf("%s 吃完!\n", name);
            phaser.arriveAndAwaitAdvance();
        }

        public void leave() {
            milliSleep(r.nextInt(1000));
            System.out.printf("%s 离开！\n", name);
            phaser.arriveAndAwaitAdvance();
        }

        public void happy() {
            if (name.equals("新娘") || name.equals("新郎")) {
                milliSleep(r.nextInt(1000));
                System.out.printf("%s 花烛夜 \n", name);
                phaser.arriveAndAwaitAdvance();
            } else {
                    //到这个阶段过滤到一些线程,让其提前结束,无须在这里继续等待
                phaser.arriveAndDeregister();
            }

        }

        @Override
        public void run() {
            arrive();
            eat();
            leave();
            happy();
        }
    }

    /**
     * 新郎 到达现场！
     * 新娘 到达现场！
     * person 2 到达现场！
     * person 3 到达现场！
     * person 4 到达现场！
     * person 1 到达现场！
     * person 0 到达现场！
     * 所有人到齐了！当前的人数==>7
     * person 4 吃完!
     * person 0 吃完!
     * person 1 吃完!
     * person 3 吃完!
     * 新娘 吃完!
     * person 2 吃完!
     * 新郎 吃完!
     * 所有人吃完了！当前的人数==>7
     * person 3 离开！
     * 新郎 离开！
     * person 1 离开！
     * person 0 离开！
     * 新娘 离开！
     * person 2 离开！
     * person 4 离开！
     * 所有人离开了！当前的人数==>7
     * 新娘 花烛夜
     * 新郎 花烛夜
     * 婚礼结束！当前的人数==>2
     *
     */
}
~~~

### ReadWriteLock 读写锁
~~~ java
/**
 * ReadWriteLock 读写锁
 * 用于读（共享锁）多写（排它锁）少的情况
 * @author peggy
 * @date 2023-03-14 14:54
 */
public class T07_ReadWriteLock {
    static ReentrantLock lock = new ReentrantLock();
    static ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    static Lock redaLock = readWriteLock.readLock();
    static Lock writeLock = readWriteLock.writeLock();

    public static void read(Lock lock) {
        lock.lock();
        try {
            TimeUnit.SECONDS.sleep(1);
            System.out.println("reda...over");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }

    }

    public static void write(Lock lock) {
        lock.lock();
        try {
            TimeUnit.SECONDS.sleep(1);
            System.out.println("write...over");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
//        无论执行那个方法都会处于阻塞的状态
//        Runnable reLock = () -> read(lock);

//        对于读的状态是一个 共享锁 允许多线程并发执行,
        Runnable reLock = () -> read(redaLock);

//        Runnable wrLock = () -> write(lock);
//        对于写状态而言,此时就是一个 排它锁 单位时间内只允许一个线程访问
        Runnable wrLock = () -> write(writeLock);

        for (int i = 0; i < 10; i++) new Thread(reLock).start();
        for (int i = 0; i < 2; i++) new Thread(wrLock).start();
    }
}
~~~

### 信号量 Semaphore 同时允许有多个线程在运行
~~~ java
/**
 * 信号量 Semaphore 同时允许有多个线程在运行
 * 应用场景
 *  - 限流
 *  - 车道与收费站
 *  - 购票 （例如余票只有 5 个只允许并发的只有 5 个 线程）
 * @author peggy
 * @date 2023-03-14 15:49
 */
public class T08_Semaphore {
    public static void main(String[] args) {
        /**
         * 当这里设置为 1 的时候此时,只限制允许一个线程执行
         * 当这里设置为 2 的时候,此时允许两个线程同时执行
         * 在这里很多人都认为这个与线程池是相同的
         *
         * 线程池是自动创建线程的
         *  而对于 Semaphore 是需要我自己手动创建线程的
         *  可参考该连接 https://blog.csdn.net/bobozai86/article/details/114004451
         */
        Semaphore semaphore = new Semaphore(1);

        new Thread(() -> {
            try {
                semaphore.acquire();
                System.out.println("线程A 在执行中。。。");
                Thread.sleep(1000);
                System.out.println("线程A 执行完毕。。。");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }finally {
                semaphore.release();
            }
        },"A").start();

        new Thread(() -> {
            try {
                semaphore.acquire();
                System.out.println("线程B 在执行中。。。");
                Thread.sleep(1000);
                System.out.println("线程B 执行完毕。。。");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }finally {
                semaphore.release();
            }
        },"B").start();
    }
}
~~~

### Exchanger 线程中的变量交换
~~~ java
/**
 *  Exchanger 线程的交换
 * @author peggy
 * @date 2023-03-14 21:16
 */
public class T09_Exchanger {
    static Exchanger<String> exchanger = new Exchanger<>();

    public static void main(String[] args) {
        new Thread(()->{
            String str="t1";
            try {
                final String s = exchanger.exchange(str);
                System.out.println("线程==>"+Thread.currentThread().getName()+"===>str==>"+s);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        },"T1").start();

        new Thread(()->{
            String str="t2";
            try {
                final String s = exchanger.exchange(str);
                System.out.println("线程==>"+Thread.currentThread().getName()+"===>str==>"+s);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        },"T2").start();
    }
}
~~~
## 第五天
### 固定容量的同步的容器
~~~ java
/**
 * 写一个固定容量的同步的容器
 * 用于 put 与 get 方法,以及 getCount 方法
 * 能够支持 2 个生产者与 10 个消费者的阻塞调用
 *
 * @author peggy
 * @date 2023-03-15 15:22
 */
public class ProducersAndConsumers {
    static List<Object> tankage = new ArrayList<>();

    public synchronized void put(Object obj) {
        tankage.add(obj);
    }

    public synchronized void get() {
        tankage.remove(1);
    }

    public synchronized int getCount() {
        return tankage.size();
    }

    public static void main(String[] args) {

        ProducersAndConsumers p = new ProducersAndConsumers();

        Thread[] producer = new Thread[2];
        Thread[] cousumer = new Thread[10];

        for (int i = 0; i < producer.length; i++) {
            producer[i] = new Thread(() -> {
                //如果有剩余消费者消费
                if (p.getCount() > 0) {
                    p.get();
                    System.out.println("消费者" + Thread.currentThread().getName() + "消费成功==> " + p.getCount());
                }
            }, "消费者-" + i);
        }

        for (int i = 0; i < cousumer.length; i++) {
            cousumer[i] = new Thread(() -> {
                //如果有剩余消费者消费
                if (p.getCount() <= 0) {
                    p.put(new Object());
                    System.out.println("生产者" + Thread.currentThread().getName() + "生产成功==> " + p.getCount());
                }
            }, "生产者-" + i);
        }

        for (Thread thread : producer) {
            thread.start();
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        for (Thread thread : cousumer) {
            thread.start();
        }

    }
}
~~~
### CountDownLath 方法测试
~~~ java
/**
 * CountDownLath 方法测试
 * @author peggy
 * @date 2023-03-15 14:38
 */
public class T11_CountDownLath {
    volatile List lists = new ArrayList<>();

    public void add(Object o) {
        lists.add(o);
    }

    public int size() {
        return lists.size();
    }

    public static void main(String[] args) {
        T11_CountDownLath countDownLath = new T11_CountDownLath();

        CountDownLatch latch = new CountDownLatch(1);

        new Thread(() -> {
            System.out.println("线程 T2 启动");
            if (countDownLath.size() != 5) {
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            System.out.println("线程 T2 结束");
        },"T2").start();

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        new Thread(() -> {
            System.out.println("线程 T1 启动");
            for (int i = 0; i < 10; i++) {
                countDownLath.add(new Object());
                System.out.println("add--->" + i);
                if (countDownLath.size() == 5) {
                    latch.countDown();
                }
                /*
                * 这里存在的问题就是,虽然当 i=5 的时候 T1 线程,执行了 countDown 方法
                * 但是由于 T2 还没来的及执行打印方法
                * T1 已经开始执行并打印
                * 当我们在这里执行休眠方法的时候,为线程 T2 提供了打印的时间
                * */
                /*try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }*/
            }
        },"T1").start();

    }
}
~~~
### CountDownLath 方法测试第二版
~~~ java
/**
 * CountDownLath 方法测试第二版
 * 为了解决上述出现的问题，需要处理就是添加在原来 T1 线程中的方法中添加一个新的门闩
 * @author peggy
 * @date 2023-03-15 14:38
 */
public class T11_CountDownLathPlus {
    volatile List lists = new ArrayList<>();

    public void add(Object o) {
        lists.add(o);
    }

    public int size() {
        return lists.size();
    }

    public static void main(String[] args) {
        T11_CountDownLathPlus countDownLath = new T11_CountDownLathPlus();

        CountDownLatch latch1 = new CountDownLatch(1);
        CountDownLatch latch2 = new CountDownLatch(1);

        new Thread(() -> {
            System.out.println("线程 T2 启动");
            if (countDownLath.size() != 5) {
                try {
                    latch1.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            System.out.println("线程 T2 结束");
            latch2.countDown();
        },"T2").start();

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        new Thread(() -> {
            System.out.println("线程 T1 启动");
            for (int i = 0; i < 10; i++) {
                countDownLath.add(new Object());
                System.out.println("add--->" + i);
                if (countDownLath.size() == 5) {
                    latch1.countDown();
                    try {
                        latch2.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        },"T1").start();

    }
}
~~~
### VarHandle 操作内存中原子性的操作
~~~ java
/**
 * VarHandle 操作内存中原子性的操作
 * 在 jdk9以后的版本中提供了 VarHandle 操作的方法
 * findVarHandle(T12_HelloVarHandle.class, "x", int.class)
 * 类对象 变量名 变量类型
 * @author peggy
 * @date 2023-03-16 10:50
 */
public class T12_HelloVarHandle {
    int x = 0;
    private static VarHandle handle;

    static {
        try {
            handle = MethodHandles.lookup().findVarHandle(T12_HelloVarHandle.class, "x", int.class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        T12_HelloVarHandle t=new T12_HelloVarHandle();

        System.out.println((int) handle.get(t));
        handle.set(t,9);
        System.out.println("当前的对象:"+t.x);

        handle.compareAndExchange(t,9,10);
        System.out.println("操作之后:"+t.x);

        handle.getAndAdd(t,10);
        System.out.println("现在的对象:"+t.x);
    }
}
~~~
### 本地多线程
~~~ java
/**
 * 本地多线程
 * @author peggy
 * @date 2023-03-16 11:12
 */
public class T13_ThreadLock {
    static ThreadLocal<User> t1  = new ThreadLocal<>();

    public static void main(String[] args) {
        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            final User user = t1.get();
            System.out.println(user);
        }).start();

        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            t1.set(new User("卡卡罗特", 18));


            /**
             *
             * ThreadLocal values pertaining to this thread. This map is maintained
             * by the ThreadLocal class.
             *
             * ThreadLocal.ThreadLocalMap threadLocals = null;
             *
             *     public void set(T value) {
             *         Thread t = Thread.currentThread(); //获取当前的线程对象
             *         ThreadLocalMap map = getMap(t); //获取当前线程对象的中 ThreadLocalMap 对象
             *         if (map != null) {
             *             map.set(this, value); //将当前线程对象做为 key
             *         } else {
             *             createMap(t, value);
             *         }
             *     }
             */
        }).start();
    }

}
~~~
### 软引用
~~~ java
/**
 * 软引用
 * -Xms20M -Xmx20M
 * 垃圾回收不一定会回收此对象,只有在空间不够的时候才会回收
 * 软引用可以用来干什么呢？
 * 一般可以做为缓存使用
 *  比如:
 *      我们需要处理一张比较大的图片,我们可以处理完后这个图片后可以将该图片先缓存起来
 *      也就是说,我们下次使用的时候可以取出该图片
 *      又或者说,我们可以中间处理的大量的数据
 *      也可以作为缓存使用
 *
 * @author peggy
 * @date 2023-03-16 17:13
 */
public class SoftQuote {
    public static void main(String[] args) {

        SoftReference<byte[]> m = new SoftReference<>(new byte[1024 * 1024 * 10]);
        System.out.println(m.get());
        System.gc();


        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println(m.get());
        //此时再分配一个数组,heap堆不够使用,这个时候系统会进行垃圾回收,如果不够软引用会被清理掉
        byte[] byte2 = new byte[1024 * 1024 * 10];

        System.out.println(m.get());
    }
}
~~~
### 强引用
~~~ java
/**
 * 强引用
 * @author peggy
 * @date 2023-03-16 17:06
 */
public class StrongQuote {
    public static void main(String[] args) {
        M m = new M();
//        m = null;
        System.gc();
        //阻塞当前的线程,由于调用 gc 的时候调用了其他的线程,所以必须先将当前的主线程进行阻塞,防止提前结束
        try {
            System.in.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
~~~
### 虚引用
~~~ java
/**
 * 虚引用
 *
 * @author peggy
 * @date 2023-03-16 19:06
 */
public class VirtualQuote {
    private final static List<Object> LIST = new ArrayList<>();
    private final static ReferenceQueue<M> QUEUE = new ReferenceQueue<>();

    public static void main(String[] args) {

        PhantomReference<M> phantomReference = new PhantomReference<>(new M(), QUEUE);

        new Thread(() -> {
            while (true) {
                // -Xms20M -Xmx20M
                LIST.add(new byte[1024 * 1024]);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    //打印跟栈信息
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
                System.out.println(phantomReference.get());
            }
        }).start();

        new Thread(() -> {
            while (true) {
                /**
                 * finlize
                 * null
                 * 虚拟机对象被 jvm 回收了----java.lang.ref.PhantomReference@1e74cb09
                 * 这里其实相当于一个监控,当监控到虚引用中的队列的中的对象被回收的时候,然后去处理回收对外内存
                 */
                //检测到对象被回收的时候,会进行动态的处理
                Reference<? extends M> reference = QUEUE.poll();
                if (reference != null) {
                    System.out.println("虚拟机对象被 jvm 回收了----" + reference);
                }
            }
        }).start();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
~~~
### 弱引用
~~~ java
/**
 * 弱引用
 * 只要发生一个 full GC 就会直接回收掉此对象
 * @author peggy
 * @date 2023-03-16 18:11
 */
public class WeakQuote {
    public static void main(String[] args) {
        //     private T referent;         /* Treated specially by GC */
        //    Reference(T referent) { this(referent, null); }
        WeakReference<M> weakReference=new WeakReference<>(new M());

        System.out.println(weakReference.get());
        System.gc();
        System.out.println(weakReference.get());


        // static class Entry extends WeakReference<ThreadLocal<?>>
        ThreadLocal<M> tl=new ThreadLocal<>();
        tl.set(new M());
        tl.remove();
    }
}
~~~