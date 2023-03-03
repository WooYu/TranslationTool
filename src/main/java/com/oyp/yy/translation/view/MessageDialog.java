package com.oyp.yy.translation.view;

import com.oyp.yy.translation.TranslationContract;

import javax.swing.*;
import java.awt.*;

/**
 * <p>date: Created by A18086 on 2019/10/19.</p>
 * <p>desc: </p>
 */
public class MessageDialog extends BasePanel {

    private JDialog jDialog;
    private JFrame jFrame;
    private JTextArea jTextArea;

    public MessageDialog(TranslationContract.Presenter presenter, JFrame jFrame) {
        super(presenter);
        this.jFrame = jFrame;
    }

    @Override
    public void initView() {
        jDialog = new JDialog(jFrame, "提示", true);
        Dimension dimension = new Dimension(500, 200);
        jDialog.setSize(dimension);
        jDialog.setResizable(false);
        jDialog.setLocationRelativeTo(jFrame);

        jTextArea = new JTextArea();
        jTextArea.setLineWrap(true);
        jTextArea.setPreferredSize(dimension);
        jTextArea.setFont(Constant.FONT18);
        jTextArea.setOpaque(true);
        jTextArea.setEditable(false);
        jTextArea.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JScrollPane jPanel = new JScrollPane(jTextArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jDialog.setContentPane(jPanel);
    }

    public boolean isShowing() {
        return jDialog.isShowing();
    }

    public void setVisible(boolean visible) {
        jDialog.setVisible(visible);
    }

    public void dispose() {
        jDialog.dispose();
    }

    public void setMessage(String msg) {
        jTextArea.setText(msg);
    }

    @Override
    public void initListener() {
    }

    /**
     * 这是个弹框，不需要返回Panel
     */
    @Override
    @Deprecated
    public JPanel getPanel() {
        return null;
    }

}
