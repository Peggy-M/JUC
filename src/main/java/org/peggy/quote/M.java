package org.peggy.quote;

/**
 * finalize() 方法是在被调用回收的时候才会执行
 * @author peggy
 * @date 2023-03-16 17:10
 */
public class M {
    @Override
    protected void finalize() throws Throwable {
        System.out.println("finlize");
    }
}
