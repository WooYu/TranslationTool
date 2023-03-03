package com.oyp.yy.translation.view;

import com.oyp.yy.translation.TranslationContract;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * <p>date: Created by A18086 on 2019/10/17.</p>
 * <p>desc: </p>
 */
public class PathSelectPanel extends BasePanel {

    private JLabel jLabel;
    private JTextField jTextField;
    private JButton jButton;
    private JPanel jPanel;
    private String defaultLabel;
    private String defaultPath;

    public PathSelectPanel(TranslationContract.Presenter presenter, String defaultLabel, String defaultPath) {
        super(presenter);
        this.defaultLabel = defaultLabel;
        this.defaultPath = defaultPath;
        setLabelText(defaultLabel);
        setPath(defaultPath);
    }

    @Override
    public void initView() {
        jPanel = new JPanel();
        jPanel.setLayout(new BorderLayout(Constant.H_GAP, Constant.V_GAP));
        jPanel.setBorder(BorderFactory.createEmptyBorder(Constant.PADDING, Constant.PADDING, Constant.PADDING, Constant.PADDING));
        jPanel.setSize(Constant.DIMENSION_PANEL);

        jLabel = new JLabel();
        jLabel.setHorizontalAlignment(SwingConstants.LEFT);
        jLabel.setPreferredSize(Constant.DIMENSION_LABEL);
        jLabel.setFont(Constant.FONT16);

        jTextField = new JTextField();
        jTextField.setBorder(Constant.BORDER);
        jTextField.setFont(Constant.FONT16);

        jButton = new JButton("选择");
        jButton.setPreferredSize(Constant.DIMENSION_BUTTON);
        jButton.setFont(Constant.FONT16);
        jButton.setVisible(false);

        jPanel.add(jLabel, BorderLayout.WEST);
        jPanel.add(jTextField, BorderLayout.CENTER);
        jPanel.add(jButton, BorderLayout.EAST);
    }

    @Override
    public JPanel getPanel() {
        return jPanel;
    }

    public void setLabelText(String text) {
        jLabel.setText(text);
    }

    public void setPath(String text) {
        jTextField.setText(text);
    }

    public String getPath() {
        return jTextField.getText();
    }

    @Override
    public void initListener() {
        jButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                presenter.selectFile(jButton, jTextField);
            }
        });
    }

    public void resetPanel() {
        setPanelEnable(true);
        jLabel.setText(defaultLabel);
        //jTextField.setText(defaultPath);
    }

    public void setPanelEnable(boolean enable) {
        setEnable(enable);
        setEditable(enable);
    }

    public void setEditable(boolean enable) {
        jTextField.setEditable(enable);
    }

    public void setEnable(boolean enable) {
        jTextField.setEnabled(enable);
    }
}
