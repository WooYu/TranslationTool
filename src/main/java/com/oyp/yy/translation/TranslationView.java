package com.oyp.yy.translation;

import com.oyp.yy.translation.view.*;
import com.oyp.yy.util.FileUtil;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>date: Created by A18086 on 2019/10/16.</p>
 * <p>desc: 多语言翻译工具View</p>
 */
public class TranslationView implements TranslationContract.View {

    private TranslationContract.Presenter presenter;

    private JFrame jFrame;
    private FunctionPanel functionPanel;
    private PathSelectPanel project1PathPanel;
    private PathSelectPanel project2PathPanel;
    private PathSelectPanel excelPathPanel;
    private PathSelectPanel languagePanel;
    private PathSelectPanel logPanel;
    private RunPanel runPanel;
    private MessageDialog msgDialog;
    private List<PathSelectPanel> pathSelectPanels = new ArrayList<>();

    public TranslationView() {
    }

    @Override
    public void initView() {
        System.out.println("TranslationView -> initView: ThreadName = " + Thread.currentThread().getName());
        // 创建一个窗口
        jFrame = new JFrame();
        jFrame.setTitle("TranslationTool"); //窗口标题
        jFrame.setSize(Constant.WINDOW_WIDTH, Constant.WINDOW_HEIGHT);
        jFrame.setLocationRelativeTo(null); //窗口屏幕居中
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); //点击关闭退出程序

        // 创建最外层Panel
        JPanel parentPanel = new JPanel();
        GridLayout layout = new GridLayout(Constant.ROW, 1, Constant.H_GAP, Constant.V_GAP);
        parentPanel.setLayout(layout);
        jFrame.setContentPane(parentPanel);

        // 功能按钮
        functionPanel = new FunctionPanel(presenter);
        parentPanel.add(functionPanel.getPanel());

        // 项目路径Panel
        project1PathPanel = new PathSelectPanel(presenter, "项目res路径", Constant.DEFAULT_PATH);
        parentPanel.add(project1PathPanel.getPanel());
        pathSelectPanels.add(project1PathPanel);

        // 第二个项目路径
        project2PathPanel = new PathSelectPanel(presenter, "项目res路径", Constant.DEFAULT_PATH);
        parentPanel.add(project2PathPanel.getPanel());
        pathSelectPanels.add(project2PathPanel);

        // ExcelPath
        excelPathPanel = new PathSelectPanel(presenter, "Excel文件夹路径", Constant.DEFAULT_PATH);
        parentPanel.add(excelPathPanel.getPanel());
        pathSelectPanels.add(excelPathPanel);

        // 语言
        languagePanel = new PathSelectPanel(presenter, "语言简写", "en,zh");
        parentPanel.add(languagePanel.getPanel());
        pathSelectPanels.add(languagePanel);

        // 日志
        logPanel = new PathSelectPanel(presenter, "日志文件路径", Constant.DEFAULT_PATH);
        parentPanel.add(logPanel.getPanel());
        logPanel.setEditable(false);
        pathSelectPanels.add(logPanel);

        // 运行
        runPanel = new RunPanel(presenter);
        parentPanel.add(runPanel.getPanel());

    }

    @Override
    public void setPresenter(TranslationContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showWindow() {
        System.out.println("TranslationView -> showWindow: ThreadName = " + Thread.currentThread().getName());
        jFrame.setVisible(true);
    }

    @Override
    public void resetPathSelectPanel() {
        for (PathSelectPanel panel : pathSelectPanels) {
            panel.resetPanel();
        }
    }

    @Override
    public void showFunction(int funType) {
        System.out.println("TranslationView -> showFunction: funType = " + funType + ", ThreadName = " + Thread.currentThread().getName());
        resetPathSelectPanel();
        switch (funType) {
            case Constant.FUN_TYPE_ADD:
                project2PathPanel.setEditable(false);
                project2PathPanel.setLabelText("生成资源xml路径");
                excelPathPanel.setLabelText("Excel文件路径");
                logPanel.setEditable(false);
                break;
            case Constant.FUN_TYPE_GENERATE:
                project2PathPanel.setPanelEnable(false);
                excelPathPanel.setLabelText("Excel文件夹路径");
                logPanel.setEditable(false);
                break;
            case Constant.FUN_TYPE_MERGE:
                excelPathPanel.setLabelText("合并文件路径");
                logPanel.setEditable(false);
                break;
            case Constant.FUN_TYPE_COMPARE:
                excelPathPanel.setPanelEnable(false);
                logPanel.setEditable(false);
                break;
        }

    }

    @Override
    public void showFileChooser(JButton jButton, JTextField jTextField) {
        System.out.println("TranslationView -> showFileChooser: ThreadName = " + Thread.currentThread().getName());
        File currentDirectory = new File(jTextField.getText());
        JFileChooser jFileChooser = new JFileChooser();
        if (currentDirectory.exists()) {
            jFileChooser.setCurrentDirectory(currentDirectory);
        }
        jFileChooser.setSize(800, 600);
        jFileChooser.setFont(Constant.FONT16);
        jFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        jFileChooser.showDialog(new JLabel(), "选择");
        File file = jFileChooser.getSelectedFile();
        if (file != null) {
            jTextField.setText(file.getAbsolutePath());
        }
    }

    @Override
    public String getProjectResPath(int which) {
        if (which == Constant.PROJECT_FIRST) {
            return project1PathPanel.getPath();
        } else if (which == Constant.PROJECT_SECOND) {
            return project2PathPanel.getPath();
        }
        return null;
    }

    @Override
    public void setProjectResPath(int which, String path) {
        if (which == Constant.PROJECT_FIRST) {
            project1PathPanel.setPath(path);
        } else if (which == Constant.PROJECT_SECOND) {
            project2PathPanel.setPath(path);
        }
    }

    @Override
    public String getExcelPath() {
        return excelPathPanel.getPath();
    }

    @Override
    public String getLogDirPath() {
        String path = Constant.DEFAULT_PATH;
        int funType = presenter.getFunctionType();
        if (funType == Constant.FUN_TYPE_ADD) {
            path = FileUtil.getParentPath(excelPathPanel.getPath());
        } else if (funType == Constant.FUN_TYPE_GENERATE) {
            path = excelPathPanel.getPath();
        } else if (funType == Constant.FUN_TYPE_MERGE) {
            path = excelPathPanel.getPath();
        }
        System.out.println("TranslationView -> getLogDirPath: path = " + path + ", ThreadName = " + Thread.currentThread().getName());
        return path;
    }

    @Override
    public void setLogPath(String path) {
        System.out.println("TranslationView -> setLogPath: path = " + path + ", ThreadName = " + Thread.currentThread().getName());
        logPanel.setPath(path);
    }

    @Override
    public String[] getLanguages() {
        String languages = languagePanel.getPath();
        return languages.split(",");
    }

    @Override
    public void setRunEnable(boolean enable) {
        System.out.println("TranslationView -> setRunEnable: enable = " + enable + ", ThreadName = " + Thread.currentThread().getName());
        runPanel.setEnable(enable);
    }

    @Override
    public void showMessage(String msg) {
        System.out.println("TranslationView -> showMessage: msg = " + msg + ", ThreadName = " + Thread.currentThread().getName());
        if (msgDialog == null) {
            msgDialog = new MessageDialog(presenter, jFrame);
        }
        msgDialog.setMessage(msg);
        if (!msgDialog.isShowing()) {
            msgDialog.setVisible(true);
        }
    }

    @Override
    public void hideMessage() {
        System.out.println("TranslationView -> hideMessage: ThreadName = " + Thread.currentThread().getName());
        if (msgDialog != null) {
            msgDialog.dispose();
            msgDialog.setMessage("");
        }
    }

    /*@Override
    public void showProgress() {
        System.out.println("TranslationView -> showProgress: ThreadName = " + Thread.currentThread().getName());
        if (jDialog == null) {
            jDialog = new JDialog(jFrame, "提示", true);
            jDialog.setSize(250, 150);
            jDialog.setResizable(false);
            jDialog.setLocationRelativeTo(jFrame);

            JProgressBar jProgressBar = new JProgressBar();
            jProgressBar.setIndeterminate(true);

            JPanel jPanel = new JPanel(new GridLayout(1, 1));
            jPanel.add(jProgressBar);

            jDialog.setContentPane(jPanel);
        }
        jDialog.setVisible(true);
    }*/

    /*@Override
    public void hideProgress() {
        System.out.println("TranslationView -> hideProgress: ThreadName = " + Thread.currentThread().getName());
        if (jDialog != null) {
            jDialog.dispose();
        }
    }*/

    @Override
    public void showError(String error) {
        System.out.println("TranslationView -> showError: error = " + error + ", ThreadName = " + Thread.currentThread().getName());
        showMessage(error);
    }

}
