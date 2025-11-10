package com.atguigu.study.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileMD5 {
    
    /**
     * 获取文件的MD5值
     * @param file 文件对象
     * @return MD5字符串
     */
    public static String getFileMD5(File file) {
        if (!file.exists() || !file.isFile()) {
            return null;
        }
        
        MessageDigest digest = null;
        FileInputStream in = null;
        byte[] buffer = new byte[8192];
        int len;
        
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            
            while ((len = in.read(buffer)) != -1) {
                digest.update(buffer, 0, len);
            }
            
            byte[] bytes = digest.digest();
            return bytesToHex(bytes);
            
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 将字节数组转换为十六进制字符串
     * @param bytes 字节数组
     * @return 十六进制字符串
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
    
    public static void main(String[] args) {
        File file = new File("test.txt");
        String md5 = getFileMD5(file);
        System.out.println("文件MD5值: " + md5);
    }
}