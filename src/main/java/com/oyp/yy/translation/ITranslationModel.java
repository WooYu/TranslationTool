package com.oyp.yy.translation;

import com.oyp.yy.BaseModel;

/**
 * <p>date: Created by A18086 on 2019/10/17.</p>
 * <p>desc: </p>
 */
public interface ITranslationModel extends BaseModel {

    /**
     * 解析翻译的Excel表格，将语言资源合并到项目中
     */
    void addTranslationExcel(String projectResDir, String excelPath, String[] languages, RunTaskCallback callback);

    /**
     * 生成翻译申请
     */
    void generateTranslationExcel(String projectResDir, String excelDir, String[] languages, RunTaskCallback callback);

    /**
     * 合并项目语言资源
     */
    void mergeProjectRes(String projectResDir1, String projectResDir2, String mergeDir, String[] languages, RunTaskCallback callback);

    /**
     * 比较语言资源
     */
    void compareRes(String projectResDir1, String projectResDir2, String[] languages, RunTaskCallback callback);

    interface RunTaskCallback {

        void onStart();

        void onSuccess();

        void onError(String error);

    }

}
