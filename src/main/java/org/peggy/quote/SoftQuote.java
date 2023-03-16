package org.peggy.quote;

import java.lang.ref.SoftReference;
import java.util.concurrent.TimeUnit;

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
