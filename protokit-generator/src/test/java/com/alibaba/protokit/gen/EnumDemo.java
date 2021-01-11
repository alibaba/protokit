package com.alibaba.protokit.gen;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

enum Mobile {
    Samsung(400), Nokia(250), Motorola(325);

    int price;

    Mobile(int p) {
        price = p;
    }

    int showPrice() {
        return price;
    }
}

class Mobile2 {
    private String s1;
    private String s2;
    private String s3;
}

public class EnumDemo {

    public static void main(String args[]) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        System.out.println("CellPhone List:");
        for (Object m : Mobile.values()) {
            Method method = m.getClass().getMethod("ordinal");
            System.out.println(m + " ordinal " + method.invoke(m));
        }
    }
}
