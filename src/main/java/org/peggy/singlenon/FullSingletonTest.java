package org.peggy.singlenon;

/**
 * 单例模式的测试
 *
 * @author peggy
 * @date 2023-03-10 15:29
 */
public class FullSingletonTest {
    //单例模式测试
    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                FullSingleton fullSingleton = FullSingleton.getFullSingleton();
                System.out.println(fullSingleton.hashCode());
            }, "i").start();
        }
    }
}
