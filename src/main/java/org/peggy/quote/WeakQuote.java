package org.peggy.quote;

import java.lang.ref.WeakReference;

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
