package org.peggy.other;

import java.util.Random;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

/**
 * Phaser 是通过变量进行控制着 不同的阶段对当前的线程进行执行
 * stage = 1  ---> 阶段1             | A | ======> 线程 A 执行
 * stage = 2  ---> 阶段2         | A | B | ======> 线程 A B执行
 * stage = 3  ---> 阶段3     | A | B | C | ======> 线程 A B C执行
 * stage = 4  ---> 阶段4 | A | B | C | D | ======> 线程 A B C D执行
 * <p>
 * 像这种的类型就是线程以此进行执行,先异步执行,再同步执行
 * 应用场景:
 * 遗传学 可以用于过滤
 *
 * @author peggy
 * @date 2023-03-14 12:20
 */
public class T06_Phaser {
    static Random r = new Random();
    static MarriagePhaser phaser = new MarriagePhaser();

    //随机时间获取
    static void milliSleep(int milli) {
        try {
            TimeUnit.MILLISECONDS.sleep(milli);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        //设置线程等待的次数
        phaser.bulkRegister(7);

        for (int i = 0; i < 5; i++) {
            final int nameIndex = i;
            new Thread(new Person("person " + nameIndex)).start();
        }
        new Thread(new Person("新郎")).start();

        new Thread(new Person("新娘")).start();

    }


    static class MarriagePhaser extends Phaser {
        //每一个被拦截的栅栏被推倒后,就会执行的方法
        @Override
        protected boolean onAdvance(int phase, int registeredParties) {
            // phase 当前所处的阶段 registeredPatiers 当前有多少线程参与了
            switch (phase) {
                case 0:
                    System.out.println("所有人到齐了！当前的人数==>" + registeredParties);
                    return false;
                case 1:
                    System.out.println("所有人吃完了！当前的人数==>" + registeredParties);
                    return false;
                case 2:
                    System.out.println("所有人离开了！当前的人数==>" + registeredParties);
                    return false;
                case 3:
                    System.out.println("婚礼结束！当前的人数==>" + registeredParties);
                    return true;
                default:
                    return true;
            }
        }
    }


    static class Person implements Runnable {
        String name;

        public Person(String name) {
            this.name = name;
        }

        public void arrive() {
            milliSleep(r.nextInt(1000));
            System.out.printf("%s 到达现场！\n", name);
            //每一个线程执行到这个方法的时候,就会等待。当达到指定数量的时候再进行执行
            phaser.arriveAndAwaitAdvance();
        }

        public void eat() {
            milliSleep(r.nextInt(1000));
            System.out.printf("%s 吃完!\n", name);
            phaser.arriveAndAwaitAdvance();
        }

        public void leave() {
            milliSleep(r.nextInt(1000));
            System.out.printf("%s 离开！\n", name);
            phaser.arriveAndAwaitAdvance();
        }

        public void happy() {
            if (name.equals("新娘") || name.equals("新郎")) {
                milliSleep(r.nextInt(1000));
                System.out.printf("%s 花烛夜 \n", name);
                phaser.arriveAndAwaitAdvance();
            } else {
                    //到这个阶段过滤到一些线程,让其提前结束,无须在这里继续等待
                phaser.arriveAndDeregister();
            }

        }

        @Override
        public void run() {
            arrive();
            eat();
            leave();
            happy();
        }
    }

    /**
     * 新郎 到达现场！
     * 新娘 到达现场！
     * person 2 到达现场！
     * person 3 到达现场！
     * person 4 到达现场！
     * person 1 到达现场！
     * person 0 到达现场！
     * 所有人到齐了！当前的人数==>7
     * person 4 吃完!
     * person 0 吃完!
     * person 1 吃完!
     * person 3 吃完!
     * 新娘 吃完!
     * person 2 吃完!
     * 新郎 吃完!
     * 所有人吃完了！当前的人数==>7
     * person 3 离开！
     * 新郎 离开！
     * person 1 离开！
     * person 0 离开！
     * 新娘 离开！
     * person 2 离开！
     * person 4 离开！
     * 所有人离开了！当前的人数==>7
     * 新娘 花烛夜
     * 新郎 花烛夜
     * 婚礼结束！当前的人数==>2
     *
     */
}
