package org.peggy.singlenon;

/**
 * 懒汉模式
 *
 * @author peggy
 * @date 2023-03-10 15:16
 */
public class FullSingleton {

    //对象在 JVM 第一次加载的时候就已经初始化，由 JVM 进行初始化,而非用户
    private static FullSingleton fullSingleton = new FullSingleton();

    //构造方法私有,不允许自己创建对象
    private FullSingleton() {
    }

    //对外只提供一个获取该对象的方法
    public static FullSingleton getFullSingleton() {
        return fullSingleton;
    }
}
