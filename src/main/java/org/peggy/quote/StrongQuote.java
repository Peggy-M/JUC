package org.peggy.quote;

import java.io.IOException;

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
