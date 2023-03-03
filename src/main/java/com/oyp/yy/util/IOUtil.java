package com.oyp.yy.util;

import java.io.Closeable;
import java.io.IOException;

/**
 * <p>date: Created by A18086 on 2019/10/18.</p>
 * <p>desc: </p>
 */
public class IOUtil {

    public static void close(Closeable closeable){
        if (closeable !=null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
