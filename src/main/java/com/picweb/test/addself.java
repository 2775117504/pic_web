package com.picweb.test;

public class addself {
    public static void main(String[] args) {
        int a = 0;
        for (int i = 0; i < 6;){
            System.out.println(i+"===="+a);
            i++;
            ++a;
        }
        System.out.println("============================");
        a = 0;
        for (int i = 0; i < 6;){
            i++;
            ++a;
            System.out.println(i+"===="+a);
        }
    }
}
