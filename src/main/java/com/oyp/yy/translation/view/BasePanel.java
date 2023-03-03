package com.oyp.yy.translation.view;

import com.oyp.yy.translation.TranslationContract;

/**
 * <p>date: Created by A18086 on 2019/10/17.</p>
 * <p>desc: </p>
 */
public abstract class BasePanel implements IUserView {

    protected TranslationContract.Presenter presenter;

    public BasePanel(TranslationContract.Presenter presenter) {
        setPresenter(presenter);
        initView();
        initListener();
    }

    @Override
    public void setPresenter(TranslationContract.Presenter presenter) {
        this.presenter = presenter;
    }

}
