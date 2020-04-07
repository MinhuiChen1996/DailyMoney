package com.example.dailymoney;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Author: Minhui Chen on 2020/4/1 15:28
 * Summary:
 */
public class MD5Utils {
    //md5 encryption algorithm
    public static String md5(String text) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("md5");
            // array byte[] result -> digest.digest( );  text text.getBytes();
            byte[] result = digest.digest(text.getBytes());
            //StringBuffer，more security
            //StringBuilder sb = new StringBuilder();
            StringBuffer sb = new StringBuffer();
            // result array，digest.digest ( ); -> text.getBytes();
            // for loop array byte[] result;
            for (byte b : result) {
                // 0xff hexadecimal
                int number = b & 0xff;
                // number value overt String Integer.toHexString( );
                String hex = Integer.toHexString(number);
                if (hex.length() == 1) {
                    sb.append("0" + hex);
                } else {
                    sb.append(hex);
                }
            }
            //sb StringBuffer sb = new StringBuffer();
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            //if exception set empty string
            return "";
        }
    }
}
