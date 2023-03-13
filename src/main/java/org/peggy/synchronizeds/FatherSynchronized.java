package org.peggy.synchronizeds;

/**
 * 父类对象加锁
 * @author peggy
 * @date 2023-03-09 15:33
 */
public class FatherSynchronized {
    Integer age = 10;
    public synchronized void setAge() {
        age--;
    }
}
