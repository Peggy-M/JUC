package org.peggy.singlenon;

/**
 * @author peggy
 * @date 2023-03-10 15:43
 */
public class HungerSingletonTest {
    static int hashCode;

    public static void main(String[] args) {
        hashCode = HungerSingleton.getHungerSingleton().hashCode();
/*        for (int i = 0; i < 10; i++) {
            if (hashCode != HungerSingleton.getHungerSingleton().hashCode()) {
                System.out.println("不一致");
            }
        }*/

       /* //多线程情况下带来的问题
        for (long i = 0; i < 100; i++) {
            new Thread(() -> {
                if (hashCode != HungerSingleton.getHungerSingleton().hashCode()) {
                    System.out.println("不一致");
                }
            },i+"").start();
        }*/

        for (int i = 0; i < 100; i++) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    System.out.println(HungerSingleton.getHungerSingleton().hashCode());
                }
            };
            Thread thread = new Thread(runnable);
            thread.start();
        }
    }
}
