package com.oyp.yy;

import com.oyp.yy.translation.TranslationContract;
import com.oyp.yy.translation.TranslationModel;
import com.oyp.yy.translation.TranslationPresenter;
import com.oyp.yy.translation.TranslationView;
import com.oyp.yy.translation.view.Constant;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class Main {

    public static void main(String[] args) {
        setErrorLog();
        // Swing 组件 需要在Event Dispatching Thread 执行
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                TranslationView view = new TranslationView();
                TranslationModel model = new TranslationModel();
                TranslationContract.Presenter presenter = new TranslationPresenter(view, model);
                presenter.start();
                presenter.selectFunction(Constant.FUN_TYPE_ADD);
            }
        });
    }

    private static void setErrorLog() {
        File file = new File("");
        try {
            String canonicalPath = file.getCanonicalPath();
            File errFile = new File(canonicalPath, "err.log");
            if (errFile.exists()) {
                errFile.delete();
                errFile.createNewFile();
            }
            System.out.println("Main -> setErrorLog: errFile = " + errFile.getPath() + ", ThreadName = " + Thread.currentThread().getName());
            System.setErr(new PrintStream(new FileOutputStream(errFile, true), true, "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
