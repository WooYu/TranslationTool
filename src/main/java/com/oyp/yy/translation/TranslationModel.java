package com.oyp.yy.translation;

import com.oyp.yy.util.FileUtil;
import com.oyp.yy.util.LogUtil;
import com.oyp.yy.util.StringUtil;
import com.oyp.yy.xmlandexcel.ResBean;
import com.oyp.yy.xmlandexcel.Util;
import com.oyp.yy.xmlandexcel.XmlExcelUtil;

import java.io.File;
import java.util.*;

/**
 * <p>date: Created by A18086 on 2019/10/17.</p>
 * <p>desc: </p>
 */
public class TranslationModel implements ITranslationModel {

    @Override
    public void addTranslationExcel(String projectResDir, String excelPath, String[] languages, RunTaskCallback callback) {
        System.out.println("TranslationModel -> addTranslationExcel: projectResDir = " + projectResDir + ", excelPath = " + excelPath +
                ", languages = " + Arrays.deepToString(languages) + ", ThreadName = " + Thread.currentThread().getName());
        LogUtil.addLog("addTranslationExcel: start");
        callback.onStart();
        try {
            File excelDir = new File(excelPath).getParentFile();

            List<List<ResBean>> multiResBeanLists = XmlExcelUtil.readExcel(excelPath);
            for (int i = 0; i < multiResBeanLists.size(); i++) {
                List<ResBean> resBeans = multiResBeanLists.get(i);
                if (resBeans != null && resBeans.size() > 0) {
                    LogUtil.addLog("addTranslationExcel: excel 第" + (i + 1) + "列是 resBean = " + resBeans.get(0));
                } else {
                    LogUtil.addLog("addTranslationExcel: excel 第" + (i + 1) + "列是空");
                }
            }
            if (multiResBeanLists.size() != languages.length) {
                throw new ArrayIndexOutOfBoundsException();
            }

            LogUtil.newLine();
            for (int i = 0; i < languages.length; i++) {
                String language = languages[i];
                language = isEn(language) ? "" : "-" + language;
                String resXmlPath = projectResDir + "\\values" + language + "\\strings.xml";
                if (!FileUtil.exists(resXmlPath)) {
                    LogUtil.addLog("addTranslationExcel: 文件不存在 resXmlPath = " + resXmlPath);
                    continue;
                }
                XmlExcelUtil.createXml(multiResBeanLists.get(i), excelDir.getPath() + "\\generated\\res\\values" + language + "\\strings.xml");
            }

            for (String language : languages) {
                language = isEn(language) ? "" : "-" + language;
                String resXmlPath = projectResDir + "\\values" + language + "\\strings.xml";
                String generatedXmlPath = excelDir.getPath() + "\\generated\\res\\values" + language + "\\strings.xml";
                String outPath = excelDir.getPath() + "\\merge\\res\\values" + language + "\\strings.xml";

                if (!FileUtil.exists(resXmlPath)) {
                    continue;
                }

                LogUtil.newLine();
                LogUtil.addLog("path1 = " + resXmlPath);
                LogUtil.addLog("path2 = " + generatedXmlPath);
                List<ResBean> stringResBeanList1 = XmlExcelUtil.readXml(resXmlPath);
                List<ResBean> stringResBeanList2 = XmlExcelUtil.readXml(generatedXmlPath);
                List<ResBean> result = Util.mergeResBeans(stringResBeanList1, stringResBeanList2, true, null);

                LogUtil.newLine();
                LogUtil.addLog("合并后的路径 outPath = " + outPath);
                XmlExcelUtil.createXml(result, outPath);
            }

            callback.onSuccess();
        } catch (ArrayIndexOutOfBoundsException e1) {
            callback.onError("语言简写数量和顺序需要和Excel表格里的语言数量和顺序一致（不包含id列）！");
        } catch (Exception e) {
            e.printStackTrace();
            callback.onError(e.toString());
        }
        LogUtil.newLine();
        LogUtil.addLog("addTranslationExcel: end");
    }

    @Override
    public void generateTranslationExcel(String projectResDir, String excelDir, String[] languages, RunTaskCallback callback) {
        System.out.println("TranslationModel -> generateTranslationExcel: projectResDir = " + projectResDir + ", excelDir = " + excelDir +
                ", languages = " + Arrays.deepToString(languages) + ", ThreadName = " + Thread.currentThread().getName());
        LogUtil.addLog("generateTranslationExcel: start");
        callback.onStart();
        try {
            for (String lan : languages) {
                if (isEnZh(lan)) {
                    continue;
                }
                LogUtil.addLog("generateTranslationExcel: smallLan = " + lan + " ===========================================================");
                LogUtil.newLine();
                //获取所有需要转换的语言
                String[] lans = new String[]{"en", "zh", lan};

                //校验能不能读xml
                List<String> filePaths = new ArrayList<>();
                for (String language : lans) {
                    String name = language;
                    if ("en".equals(name)) {
                        name = "";
                    } else {
                        name = "-" + name;
                    }
                    String filePath = projectResDir + "\\values" + name + "\\strings.xml";
                    filePaths.add(filePath);
                }

                //读xml
                List<List<ResBean>> multiResBeanList = new ArrayList<>();
                for (int i = 0; i < lans.length; i++) {
                    if (FileUtil.exists(filePaths.get(i))) {
                        multiResBeanList.add(XmlExcelUtil.readXml(filePaths.get(i), true));
                    } else {
                        multiResBeanList.add(new ArrayList<>());
                    }
                }

                //删除英语不存在的id，并使各个集合的资源id数量和位置与英语一样
                Util.deleteIdNotExitInEnAndAlign(multiResBeanList);

                //删除不需要翻译的文本
//                Util.deleteNoNeedTranslate(multiResBeanList);

                // excel 表格命名
                File file = new File(excelDir);
                if (file.exists() && !file.isDirectory()) {
                    throw new RuntimeException(excelDir + " is not a directory!");
                }
                int length = 0;
                for (ResBean enResBean : multiResBeanList.get(0)) {
                    length += enResBean.value.split(" ").length;
                }
                String name = "EN_2_" + lan + "_" + length;
                File excelFile = new File(excelDir, name.toUpperCase(Locale.ENGLISH));
                FileUtil.mkdirs(excelFile.getParent());

                // 生成表格
                XmlExcelUtil.createExcel(multiResBeanList, excelFile.getAbsolutePath() + ".xlsx");
            }
            callback.onSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            callback.onError(e.toString());
        }
        LogUtil.addLog("generateTranslationExcel: end");
    }

    @Override
    public void mergeProjectRes(String projectResDir1, String projectResDir2, String mergeDir, String[] languages, RunTaskCallback callback) {
        System.out.println("TranslationModel -> mergeProjectRes: projectResDir1 = " + projectResDir1 + ", projectResDir2 = " + projectResDir2 + ", mergeDir = " + mergeDir +
                ", languages = " + Arrays.deepToString(languages) + ", ThreadName = " + Thread.currentThread().getName());
        LogUtil.addLog("mergeProjectRes: start");
        callback.onStart();
        try {
            FileUtil.mkdirs(mergeDir);

            for (String language : languages) {
                String lan = isEn(language) ? "" : "-" + language;
                String xmlPath1 = projectResDir1 + "\\values" + lan + "\\strings.xml";
                String xmlPath2 = projectResDir2 + "\\values" + lan + "\\strings.xml";
                String mergePath = mergeDir + "\\values" + lan + "\\strings.xml";

                LogUtil.newLine(2);
                LogUtil.addLog("-------------------------------- language = " + language + " --------------------------------");
                List<ResBean> resBeans1 = XmlExcelUtil.readXml(xmlPath1);
                List<ResBean> resBeans2 = XmlExcelUtil.readXml(xmlPath2);
                List<ResBean> resBeans = Util.mergeResBeans(resBeans1, resBeans2, true, null);
                XmlExcelUtil.createXml(resBeans, mergePath, "path1 = " + xmlPath1 + ", path2 = " + xmlPath2);
            }

            callback.onSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            callback.onError(e.toString());
        }
        LogUtil.addLog("mergeProjectRes: end");
    }

    @Override
    public void compareRes(String projectResDir1, String projectResDir2, String[] languages, RunTaskCallback callback) {
        System.out.println("TranslationModel -> compareRes: projectResDir1 = " + projectResDir1 + ", projectResDir2 = " + projectResDir2 +
                ", languages = " + Arrays.deepToString(languages) + ", ThreadName = " + Thread.currentThread().getName());
        LogUtil.addLog("compareRes: start");
        callback.onStart();
        try {

            for (String language : languages) {
                String lan = isEn(language) ? "" : "-" + language;
                String xmlPath1 = projectResDir1 + "\\values" + lan + "\\strings.xml";
                String xmlPath2 = projectResDir2 + "\\values" + lan + "\\strings.xml";

                LogUtil.newLine(2);
                LogUtil.addLog("path1 = " + xmlPath1 + ", path2 = " + xmlPath2);
                LogUtil.newLine();

                List<ResBean> resBeans1 = XmlExcelUtil.readXml(xmlPath1);
                List<ResBean> resBeans2 = XmlExcelUtil.readXml(xmlPath2);

                Iterator<ResBean> iterator = resBeans1.iterator();
                while (iterator.hasNext()) {
                    ResBean resBean1 = iterator.next();
                    for (int j = 0; j < resBeans2.size(); j++) {
                        ResBean resBean2 = resBeans2.get(j);
                        if (resBean1.resId.equals(resBean2.resId)) {
                            if (!resBean1.value.equals(resBean2.value) && resBean1.isNeedTranslate() && resBean2.isNeedTranslate()) {
                                LogUtil.addLog("资源value不一样：" +
                                        "\n\tresBean1 = " + resBean1 +
                                        "\n\tresBean2 = " + resBean2);
                            }
                            iterator.remove();
                            resBeans2.remove(j);
                            break;
                        }
                    }
                }

                LogUtil.newLine();
                if (resBeans1.size() > 0) {
                    LogUtil.addLog(xmlPath1 + "存在，而" + xmlPath2 + "不存在的资源 size = " + resBeans1.size() + "\n");
                    for (ResBean resBean : resBeans1) {
                        LogUtil.addLog("resBean1 = " + resBean);
                    }
                }

                if (resBeans2.size() > 0) {
                    LogUtil.newLine();
                    LogUtil.addLog(xmlPath2 + "存在，而" + xmlPath1 + "不存在的资源 size = " + resBeans2.size() + "\n");
                    for (ResBean resBean : resBeans2) {
                        LogUtil.addLog("resBean2 = " + resBean);
                    }
                }
            }

            callback.onSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            callback.onError(e.toString());
        }
        LogUtil.addLog("TranslationModel -> compareRes: end");
    }

    private boolean isEn(String lan) {
        return StringUtil.equals("en", lan);
    }

    private boolean isZh(String lan) {
        return StringUtil.equals("zh", lan);
    }

    private boolean isZh_rTw(String lan) {
        return StringUtil.equals("zh-rTw", lan);
    }

    private boolean isEnZh(String lan) {
        return isEn(lan) || isZh(lan);
    }

}
