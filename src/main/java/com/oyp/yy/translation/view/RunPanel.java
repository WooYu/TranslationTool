package com.oyp.yy.translation.view;

import com.oyp.yy.translation.TranslationContract;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * <p>date: Created by A18086 on 2019/10/17.</p>
 * <p>desc: </p>
 */
public class RunPanel extends BasePanel {

    private JPanel jPanel;
    private JButton jButton;

    public RunPanel(TranslationContract.Presenter presenter) {
        super(presenter);
    }

    @Override
    public void initView() {
        jPanel = new JPanel();
        jPanel.setLayout(new BorderLayout(Constant.H_GAP, Constant.V_GAP));
        jPanel.setBorder(BorderFactory.createEmptyBorder(Constant.PADDING, Constant.PADDING, Constant.PADDING, Constant.PADDING));
        jPanel.setSize(Constant.DIMENSION_PANEL);

        jButton = new JButton("运行");
        jButton.setPreferredSize(Constant.DIMENSION_BUTTON);
        jButton.setFont(Constant.FONT16);

        jPanel.add(jButton, BorderLayout.CENTER);
    }

    @Override
    public void initListener() {
        jButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                presenter.runTask();
            }
        });
    }

    @Override
    public JPanel getPanel() {
        return jPanel;
    }

    public void setEnable(boolean enable) {
        jButton.setEnabled(enable);
    }

}
