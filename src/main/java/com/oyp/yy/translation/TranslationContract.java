package com.oyp.yy.translation;

import com.oyp.yy.BasePresenter;
import com.oyp.yy.BaseView;

import javax.swing.*;

/**
 * <p>date: Created by A18086 on 2019/10/16.</p>
 * <p>desc: </p>
 */
public interface TranslationContract {

    interface Presenter extends BasePresenter {

        void selectFunction(int funType);

        void runTask();

        void selectFile(JButton jButton, JTextField jTextField);

        int getFunctionType();

    }

    interface View extends BaseView<Presenter> {

        void initView();

        void showWindow();

        void resetPathSelectPanel();

        void showFunction(int funType);

        void showFileChooser(JButton jButton, JTextField jTextField);

        String getProjectResPath(int which);

        void setProjectResPath(int which, String path);

        String getExcelPath();

        void setLogPath(String path);

        String getLogDirPath();

        String[] getLanguages();

        void setRunEnable(boolean enable);

        //void showProgress();

        //void hideProgress();

        void showMessage(String msg);

        void hideMessage();

        void showError(String error);

    }


}
