package com.infobip.util;

public class Util {
	
    public static boolean isEmpty(String str) {
        if (str == null || str.trim().length() == 0)
            return true;
        else
            return false;
    }
}
