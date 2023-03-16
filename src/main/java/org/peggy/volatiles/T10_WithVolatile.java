package org.peggy.volatiles;

/**
 * volatile 修饰的对象内部改变的不可见性
 * 修饰的如果修饰的是对象,对象的引用改变是可见的
 * 但对于对象内部的值发生改变,是不可变的
 * 比如：
 *   volatile User user=new User("特兰克斯",18);        修饰不可见
 *   user=new User("卡卡罗特",18);          修饰可见的
 *   user.name("布罗利"); 不可见
 *   volatile
 * @author peggy
 * @date 2023-03-15 14:04
 */
public class T10_WithVolatile {

}
