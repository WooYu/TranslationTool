package com.oyp.yy.util;

import java.io.File;

/**
 * <p>date: Created by A18086 on 2019/10/18.</p>
 * <p>desc: </p>
 */
public class FileUtil {

    private FileUtil() {
        throw new RuntimeException("don`t do this");
    }

    public static void mkdirs(String path) {
        File file = new File(path);
        if (!file.exists()) {
            boolean mkdirs = file.mkdirs();
            System.out.println("FileUtil -> mkdirs: path = " + path + ", mkdirs = " + mkdirs);
        }
    }

    public static boolean exists(String path){
        File file = new File(path);
        return file.exists();
    }

    public static String getParentPath(String path){
        return new File(path).getParent();
    }

}
