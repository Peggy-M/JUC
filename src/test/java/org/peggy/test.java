package org.peggy;

/**
 * @author peggy
 * @date 2023-03-14 17:16
 */
public class test {
    public static void main(String[] args) {
        try {
            int i=1/0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println("-----------------");
    }
}
