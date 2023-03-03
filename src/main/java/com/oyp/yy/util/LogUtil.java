package com.oyp.yy.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <p>date: Created by A18086 on 2019/10/18.</p>
 * <p>desc: </p>
 */
public class LogUtil {

    private static String path;
    private static BufferedWriter writer;

    private LogUtil() {
        throw new RuntimeException("don`t do this!");
    }

    public static void startLog(String dir) {
        createLogFile(dir);
        endLog();
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path, true), StandardCharsets.UTF_8));
            newLine();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh-mm-ss");
            addLog("===============" + sdf.format(new Date()) + "=============== start");
            newLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addLog(String log) {
        if (writer == null) {
            return;
        }
        try {
            writer.write(log);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void newLine() {
        if (writer == null) {
            return;
        }
        try {
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void newLine(int number) {
        for (int i = 0; i < number; i++) {
            newLine();
        }
    }

    public static void endLog() {
        newLine();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh-mm-ss");
        addLog("===============" + sdf.format(new Date()) + "=============== end");
        newLine();
        if (writer == null) {
            return;
        }
        try {
            writer.flush();
            IOUtil.close(writer);
            writer = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getLogPath() {
        return path;
    }

    private static void createLogFile(String dir) {
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String name = sdf.format(new Date()) + ".txt";
        File file = new File(dir, name);
        FileUtil.mkdirs(file.getParent());
        path = file.getAbsolutePath();
    }


}
