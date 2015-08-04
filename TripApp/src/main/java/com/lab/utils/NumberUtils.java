package com.lab.utils;



public class NumberUtils {

    public static boolean doubuleIsZero(double v) {
        return v <= 0.0000001;
    }

    public static float paserFloat(String value){
        try {
            return Float.valueOf(value);
        }catch (Exception e){
            return 0;
        }
    }

}
