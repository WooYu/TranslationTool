package com.oyp.yy.translation.view;

import com.oyp.yy.translation.TranslationContract;
import com.oyp.yy.util.StringUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * <p>date: Created by A18086 on 2019/10/17.</p>
 * <p>desc: </p>
 */
public class FunctionPanel extends BasePanel {

    private static final Color SELECT_BACK = new Color(37, 90, 241);
    private static final Color SELECT_FONT = new Color(255, 255, 255);
    private static final Color NOT_SELECT_BACK = new Color(203, 203, 203);
    private static final Color NOT_SELECT_FONT = new Color(66, 66, 66);

    private JLabel labelAdd;
    private JLabel labelMerge;
    private JLabel labelGenerate;
    private JLabel labelCompare;
    private JLabel current;
    private JPanel jPanel;

    public FunctionPanel(TranslationContract.Presenter presenter) {
        super(presenter);
    }

    @Override
    public void initView() {
        jPanel = new JPanel();
        jPanel.setLayout(new GridLayout(1, 5, Constant.H_GAP, Constant.V_GAP));
        jPanel.setBorder(BorderFactory.createEmptyBorder(Constant.PADDING, Constant.PADDING, Constant.PADDING, Constant.PADDING));
        jPanel.setSize(Constant.DIMENSION_PANEL);

        String tipText = "解析Excel文件，生成strings.xml资源文件，添加新增翻译内容到项目资源中，对于重复的资源id，会进行覆盖（英语除外）。";
        labelAdd = createFunLabel("添加翻译内容", tipText);

        labelGenerate = createFunLabel("生成翻译申请", null);

        labelMerge = createFunLabel("合并项目资源", null);

        labelCompare = createFunLabel("比较资源文件", null);

        jPanel.add(labelAdd);
        jPanel.add(labelGenerate);
        jPanel.add(labelMerge);
        jPanel.add(labelCompare);

        setSelectFunction(labelAdd);
    }

    private JLabel createFunLabel(String text, String tipText) {
        JLabel jLabel = new JLabel(text, SwingConstants.CENTER);
        jLabel.setPreferredSize(Constant.DIMENSION_LABEL);
        jLabel.setFont(Constant.FONT16);
        jLabel.setOpaque(true);
        setSelect(jLabel, false);
        if (StringUtil.isEmpty(tipText)) {
            setToolTipText(jLabel, tipText);
        }
        return jLabel;
    }

    @Override
    public JPanel getPanel() {
        return jPanel;
    }

    @Override
    public void initListener() {
        labelAdd.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setSelectFunction((JLabel) e.getComponent());
                presenter.selectFunction(Constant.FUN_TYPE_ADD);
            }
        });
        labelMerge.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setSelectFunction((JLabel) e.getComponent());
                presenter.selectFunction(Constant.FUN_TYPE_MERGE);
            }
        });
        labelGenerate.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setSelectFunction((JLabel) e.getComponent());
                presenter.selectFunction(Constant.FUN_TYPE_GENERATE);
            }
        });
        labelCompare.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setSelectFunction((JLabel) e.getComponent());
                presenter.selectFunction(Constant.FUN_TYPE_COMPARE);
            }
        });
    }

    private void setSelectFunction(JLabel label) {
        if (label == current) {
            return;
        }
        setSelect(label, true);
        setSelect(current, false);
        current = label;
    }

    private void setSelect(JLabel label, boolean select) {
        if (label == null) {
            return;
        }
        if (select) {
            label.setForeground(SELECT_FONT);
            label.setBackground(SELECT_BACK);
        } else {
            label.setForeground(NOT_SELECT_FONT);
            label.setBackground(NOT_SELECT_BACK);
        }
    }

    private void setToolTipText(JLabel label, String tipText) {
        label.putClientProperty(JLabel.TOOL_TIP_TEXT_KEY, tipText);
        ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
        toolTipManager.setDismissDelay(5000);
        toolTipManager.registerComponent(label);
    }

}
