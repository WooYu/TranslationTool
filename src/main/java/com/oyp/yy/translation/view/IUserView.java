package com.oyp.yy.translation.view;

import com.oyp.yy.BaseView;
import com.oyp.yy.translation.TranslationContract;

import javax.swing.*;

/**
 * <p>date: Created by A18086 on 2019/10/17.</p>
 * <p>desc: </p>
 */
public interface IUserView extends BaseView<TranslationContract.Presenter> {

    void initView();

    void initListener();

    JPanel getPanel();

}
