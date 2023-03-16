package org.peggy.other;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

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
