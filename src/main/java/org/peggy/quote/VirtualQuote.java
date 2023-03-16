package org.peggy.quote;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;
import java.util.List;

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
