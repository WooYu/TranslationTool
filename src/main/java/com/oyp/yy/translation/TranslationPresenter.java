package com.oyp.yy.translation;

import com.oyp.yy.translation.view.Constant;
import com.oyp.yy.util.FileUtil;
import com.oyp.yy.util.LogUtil;
import com.oyp.yy.util.ThreadManager;

import javax.swing.*;

/**
 * <p>date: Created by A18086 on 2019/10/16.</p>
 * <p>desc: </p>
 */
public class TranslationPresenter implements TranslationContract.Presenter, Runnable {

    private TranslationContract.View view;
    private ITranslationModel model;

    private int funType = Constant.FUN_TYPE_ADD;

    private ITranslationModel.RunTaskCallback callback = new ITranslationModel.RunTaskCallback() {
        @Override
        public void onStart() {
            System.out.println("TranslationPresenter -> onStart: ThreadName = " + Thread.currentThread().getName());
            SwingUtilities.invokeLater(() -> {
                view.setRunEnable(false);
                view.setLogPath(LogUtil.getLogPath());
                if (funType == Constant.FUN_TYPE_ADD) {
                    view.setProjectResPath(2, FileUtil.getParentPath(view.getExcelPath()));
                }
                //view.showProgress();
                view.showMessage("正在执行任务。。。");
            });
        }

        @Override
        public void onSuccess() {
            System.out.println("TranslationPresenter -> onSuccess: ThreadName = " + Thread.currentThread().getName());
            SwingUtilities.invokeLater(() -> {
                view.setRunEnable(true);
                //view.hideProgress();
                //view.hideMessage();
                view.showMessage("完成！");
            });
        }

        @Override
        public void onError(String error) {
            System.out.println("TranslationPresenter -> onError: error = " + error + ", ThreadName = " + Thread.currentThread().getName());
            SwingUtilities.invokeLater(() -> {
                view.setRunEnable(true);
                //view.hideProgress();
                view.showError(error);
            });
        }
    };

    public TranslationPresenter(TranslationContract.View view, TranslationModel model) {
        this.view = view;
        this.model = model;
        this.view.setPresenter(this);
    }

    @Override
    public void start() {
        view.initView();
        view.showWindow();
    }

    @Override
    public void selectFunction(int funType) {
        this.funType = funType;
        view.showFunction(funType);
    }

    @Override
    public void runTask() {
        System.out.println("TranslationPresenter -> runTask: ThreadName = " + Thread.currentThread().getName());
        ThreadManager.getSingleThread().execute(this);
    }

    @Override
    public void run() {
        LogUtil.startLog(view.getLogDirPath());
        switch (funType) {
            case Constant.FUN_TYPE_ADD:
                model.addTranslationExcel(view.getProjectResPath(1), view.getExcelPath(), view.getLanguages(), callback);
                break;
            case Constant.FUN_TYPE_GENERATE:
                model.generateTranslationExcel(view.getProjectResPath(Constant.PROJECT_FIRST), view.getExcelPath(), view.getLanguages(), callback);
                break;
            case Constant.FUN_TYPE_MERGE:
                model.mergeProjectRes(view.getProjectResPath(Constant.PROJECT_FIRST), view.getProjectResPath(Constant.PROJECT_SECOND), view.getExcelPath(), view.getLanguages(), callback);
                break;
            case Constant.FUN_TYPE_COMPARE:
                model.compareRes(view.getProjectResPath(Constant.PROJECT_FIRST), view.getProjectResPath(Constant.PROJECT_SECOND), view.getLanguages(), callback);
                break;
        }
        LogUtil.endLog();
    }

    @Override
    public void selectFile(JButton jButton, JTextField jTextField) {
        view.showFileChooser(jButton, jTextField);
    }

    @Override
    public int getFunctionType() {
        return funType;
    }

}
