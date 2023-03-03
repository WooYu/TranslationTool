package com.oyp.yy.xmlandexcel;

import com.oyp.yy.util.LogUtil;
import com.oyp.yy.util.StringUtil;

import java.io.File;
import java.util.*;

/**
 * Created by A18086 on 2018/5/29
 */
public class Util {

    private static final String TAG = "Util：";

    public static String specialText(String source) {
        StringBuilder result = new StringBuilder();
        char[] chars = source.toCharArray();
        for (char ch : chars) {
            //LogUtil.addLog(TAG + " ch = " + ch + " ASCII = " + (ch + 0));
            switch (ch) {   //在xml内容里只有& < > 三个特殊字符不能用
                case 38:
                    result.append("&amp;"); // &
                    break;
                case 60:
                    result.append("&lt;");  // <
                    break;
                case 62:
                    result.append("&gt;");  // >
                    break;
                case 160:
                    result.append("&#160;");// 160空格，平时的空格是32
                    break;
                /*case 33:
                    result.append("&#033;"); // !
                    break;
                case 34:
                    result.append("&#034;"); // "
                    break;
                case 42:
                    result.append("&#042;"); // *
                    break;
                case 47:
                    result.append("&#047;"); // /
                    break;
                case 58:
                    result.append("&#058;"); // :
                    break;
                case 63:
                    result.append("&#063;"); // ?
                    break;
                case 180:
                    result.append("&#180;"); // ´
                    break;
                case 8230:
                    result.append("&#8230;");// ...
                    break;*/
                default:
                    result.append(ch);
                    break;
            }
        }
        return result.toString();
    }

    /**
     * 解析Excel时，处理单个特殊字符，(& -- &amp;)  (< -- &lt;) (> -- &gt;)
     *
     * @param source 原字符串
     * @return 处理后的字符串 (& -- &amp;)  (< -- &lt;) (> -- &gt;)
     */
    public static String handleSpecialChar(String source) {
        boolean hasAnd = source.contains("&") && !source.contains("&amp;") && !source.contains("&lt;") && !source.contains("&gt;") && !source.contains("&#");
        boolean hasLess = source.contains("<");
        boolean hasGreater = source.contains(">");

        if (hasAnd) {
            source = source.replaceAll("&", "&amp;");
        }
        if (hasLess) {
            source = source.replaceAll("<", "&lt;");
        }
        if (hasGreater) {
            source = source.replaceAll(">", "&gt;");
        }

        return source;
    }

    /**
     * 删除英语中不存在的id项，index=0位置的是英语，并且对齐id和数量
     */
    public static void deleteIdNotExitInEnAndAlign(List<List<ResBean>> multiResBeanList) {
        //找出长度最大的集合
        int enIndex = 0;

        //第一轮循环，使所有集合顺序与最长集合的一致，并将最长集合的原不存在的元素添加进来
        List<ResBean> enResBeans = multiResBeanList.get(enIndex);
        for (int i = 0; i < multiResBeanList.size(); i++) {
            if (i == enIndex) {
                continue;
            }
            List<ResBean> resBeanList = multiResBeanList.get(i);
            List<ResBean> tempList = new ArrayList<>(resBeanList);
            resBeanList.clear();

            for (ResBean enResBean : enResBeans) {
                int index = tempList.indexOf(enResBean);
                if (index >= 0) {
                    resBeanList.add(tempList.get(index));
                    tempList.remove(index);
                    LogUtil.addLog(Util.TAG + "删除原集合index, 添加到新集合");
                } else {
                    ResBean resBean = new ResBean();
                    resBean.resId = enResBean.resId;
                    resBean.value = "";
                    resBeanList.add(resBean);
                    LogUtil.addLog(Util.TAG + " 第一轮循环：添加不存在的resId到非最长集合 resBean = " + resBean);
                }
            }

            //剩余长度不为0
            if (tempList.size() > 0) {
                for (ResBean resBean : tempList) {
                    LogUtil.addLog(Util.TAG + "删除英语中不存在的resId, resBean = " + resBean);
                }
            }
        }

        //第二轮循环，使所有集合长度一致，元素位置一致
        for (int i = 0; i < multiResBeanList.size(); i++) {
            if (i == enIndex) {
                continue;
            }
            List<ResBean> resBeanList = multiResBeanList.get(i);
            if (enResBeans.size() > resBeanList.size()) {
                for (int j = resBeanList.size(); j < enResBeans.size(); j++) {
                    ResBean enResBean = enResBeans.get(j);
                    ResBean resBean = new ResBean();
                    resBean.resId = enResBean.resId;
                    resBean.value = "";
                    resBeanList.add(resBean);
                    LogUtil.addLog(Util.TAG + "补全英语中多的resId到其他语言 resId = " + resBean.resId);
                }
            }
        }


    }

    /**
     * 删除不需要翻译的内容
     *
     * @param multiResBeanList 分别是 英语，中文，小语种
     */
    public static void deleteNoNeedTranslate(List<List<ResBean>> multiResBeanList) {
        if (multiResBeanList.size() != 3) {
            throw new RuntimeException("multiResBeanList.size() != 3");
        }

        List<ResBean> enResBeans = multiResBeanList.get(0);
        List<ResBean> zhResBeans = multiResBeanList.get(1);
        List<ResBean> smallResBeans = multiResBeanList.get(2);

        List<ResBean> sameEnResBeans = new ArrayList<>();
        List<ResBean> sameZhResBeans = new ArrayList<>();
        List<ResBean> sameSmallResBeans = new ArrayList<>();

        System.out.println("Util -> deleteNoNeedTranslate: 1 enResBeans.size() = " + enResBeans.size() + ", sameEnResBeans.size() = " + sameEnResBeans.size());

        // 英语相同
        for (int i = 0; i < enResBeans.size(); i++) {
            ResBean enResBean = enResBeans.get(i);
            for (int j = i + 1; j < enResBeans.size(); j++) {
                ResBean anotherEnResBean = enResBeans.get(j);
                if (StringUtil.equals(enResBean.value, anotherEnResBean.value)) {
                    if (!sameEnResBeans.contains(enResBean)) {
                        sameEnResBeans.add(enResBean);
                        sameZhResBeans.add(zhResBeans.get(i));
                        sameSmallResBeans.add(smallResBeans.get(i));
                    }
                    if (!sameEnResBeans.contains(anotherEnResBean)) {
                        sameEnResBeans.add(anotherEnResBean);
                        sameZhResBeans.add(zhResBeans.get(j));
                        sameSmallResBeans.add(smallResBeans.get(j));
                    }
                }
            }
        }

        // 把英语文本相同的先移除
        enResBeans.removeAll(sameEnResBeans);
        zhResBeans.removeAll(sameZhResBeans);
        smallResBeans.removeAll(sameSmallResBeans);
        System.out.println("Util -> deleteNoNeedTranslate: 2 enResBeans.size() = " + enResBeans.size() + ", sameEnResBeans.size() = " + sameEnResBeans.size());

        // 删除英文相同，中文相同的
        for (int i = 0; i < sameEnResBeans.size(); i++) {
            ResBean enResBean = sameEnResBeans.get(i);
            for (int j = i + 1; j < sameEnResBeans.size(); j++) {
                ResBean anotherEnResBean = sameEnResBeans.get(j);
                if (StringUtil.equals(enResBean.value, anotherEnResBean.value)) {   // 英语文本一样，
                    ResBean zhResBean = sameZhResBeans.get(i);
                    ResBean anotherZhResBean = sameZhResBeans.get(j);
                    if (StringUtil.equals(zhResBean.value, anotherZhResBean.value)) { // 判断中文是否一样，如果中文也一样，只保留一个
                        sameEnResBeans.remove(j);
                        sameZhResBeans.remove(j);
                        sameSmallResBeans.remove(j);
                        j--;

                        LogUtil.newLine();
                        LogUtil.addLog(TAG + " 中英文文本完全一样，删除其中一个");
                        LogUtil.addLog(enResBean.toString());
                        LogUtil.addLog(zhResBean.toString());
                        LogUtil.addLog(anotherEnResBean.toString());
                        LogUtil.addLog(anotherZhResBean + " ------------------------- > 删除！");
                    }
                }
            }
        }

        for (int i = 0; i < sameEnResBeans.size(); i++) {
            ResBean enResBean = sameEnResBeans.get(i);
            for (int j = i + 1; j < sameEnResBeans.size(); j++) {
                ResBean anotherEnResBean = sameEnResBeans.get(j);
                if (StringUtil.equals(enResBean.value, anotherEnResBean.value)) {   // 英语文本一样，
                    ResBean zhResBean = sameZhResBeans.get(i);
                    ResBean anotherZhResBean = sameZhResBeans.get(j);
                    if (enResBean.attributes != null && StringUtil.equals("true", enResBean.attributes.get("keep"))) {
                        sameEnResBeans.remove(j);
                        sameZhResBeans.remove(j);
                        sameSmallResBeans.remove(j);
                        j--;

                        LogUtil.newLine();
                        LogUtil.addLog(TAG + " 英文文本一样，并且有一个标记为keep，删除另一个");
                        LogUtil.addLog(enResBean.toString());
                        LogUtil.addLog(zhResBean.toString());
                        LogUtil.addLog(anotherEnResBean.toString());
                        LogUtil.addLog(anotherZhResBean + "------------------------- > 删除！");
                    } else if (anotherEnResBean.attributes != null && StringUtil.equals("true", anotherEnResBean.attributes.get("keep"))) {
                        sameEnResBeans.remove(i);
                        sameZhResBeans.remove(i);
                        sameSmallResBeans.remove(i);
                        i--;

                        LogUtil.newLine();
                        LogUtil.addLog(TAG + " 英文文本一样，并且有一个标记为keep，删除另一个");
                        LogUtil.addLog(enResBean.toString() + "------------------------- > 删除！");
                        LogUtil.addLog(zhResBean.toString());
                        LogUtil.addLog(anotherEnResBean.toString());
                        LogUtil.addLog(anotherZhResBean.toString());
                        break;
                    }
                }
            }
        }

        for (int i = 0; i < sameEnResBeans.size(); i++) {
            ResBean enResBean = sameEnResBeans.get(i);
            for (int j = i + 1; j < sameEnResBeans.size(); j++) {
                ResBean anotherEnResBean = sameEnResBeans.get(j);
                if (StringUtil.equals(enResBean.value, anotherEnResBean.value)) {   // 英语文本一样，
                    ResBean zhResBean = sameZhResBeans.get(i);
                    ResBean anotherZhResBean = sameZhResBeans.get(j);
                    LogUtil.newLine();
                    LogUtil.addLog(TAG + " 英文文本一样，中文不同，需要手动删除其中一个");
                    LogUtil.addLog(enResBean.toString());
                    LogUtil.addLog(zhResBean.toString());
                    LogUtil.addLog(anotherEnResBean.toString());
                    LogUtil.addLog(anotherZhResBean.toString());
                }
            }
        }

        // 把英语文本相同的添加回来
        zhResBeans.addAll(0, sameZhResBeans);
        enResBeans.addAll(0, sameEnResBeans);
        smallResBeans.addAll(0, sameSmallResBeans);

        System.out.println("Util -> deleteNoNeedTranslate: 3 enResBeans.size() = " + enResBeans.size() + ", sameEnResBeans.size() = " + sameEnResBeans.size());

//        //英语有文本，中文没有
//        LogUtil.newLine();
//        LogUtil.addLog(TAG + " ======================= 英文有文本，中文无文本，删除 ↓ ===========================");
//        for (int i = 0; i < enResBeans.size(); i++) {
//            ResBean zhResBean = zhResBeans.get(i);
//            ResBean enResBean = enResBeans.get(i);
//            if (StringUtil.isEmpty(zhResBean.value)) {
//                enResBeans.remove(i);
//                zhResBeans.remove(i);
//                smallResBeans.remove(i);
//                LogUtil.newLine();
//
//                LogUtil.addLog(enResBean.toString());
//                LogUtil.addLog(zhResBean.toString());
//                i--;
//            }
//        }

        // 翻译文本是数字，也不需要翻译
        LogUtil.newLine();
        LogUtil.addLog(TAG + " ======================= 数字 删除 ↓ ===========================");
        for (int i = 0; i < enResBeans.size(); i++) {
            ResBean enResBean = enResBeans.get(i);
            if (enResBean.value.startsWith("%")) {
                try {
                    Double.parseDouble(enResBean.value.substring(1));
                    LogUtil.addLog(enResBean.toString());
                    enResBeans.remove(i);
                    zhResBeans.remove(enResBean);
                    smallResBeans.remove(enResBean);
                    i--;
                } catch (NumberFormatException ignored) {
                }
            } else {
                try {
                    Double.parseDouble(enResBean.value);
                    LogUtil.addLog(enResBean.toString());
                    enResBeans.remove(i);
                    zhResBeans.remove(enResBean);
                    smallResBeans.remove(enResBean);
                    i--;
                } catch (NumberFormatException ignored) {
                }
            }
        }

        //删除已翻译的部分
        Iterator<ResBean> iterator = smallResBeans.iterator();
        while (iterator.hasNext()) {
            ResBean smallResBean = iterator.next();
            if (smallResBean.value == null || smallResBean.value.equals("")) {
                continue;
            }
            for (int i = 0; i < enResBeans.size(); i++) {
                ResBean resBean = enResBeans.get(i);
                if (resBean.resId.equals(smallResBean.resId)) {
                    enResBeans.remove(i);
                    break;
                }
                if (i == enResBeans.size() - 1) {
                    LogUtil.addLog("英语不含小语种对应的资源 smallResBean = " + smallResBean);
                }
            }
            for (int i = 0; i < zhResBeans.size(); i++) {
                ResBean resBean = zhResBeans.get(i);
                if (resBean.resId.equals(smallResBean.resId)) {
                    zhResBeans.remove(i);
                    break;
                }
                if (i == zhResBeans.size() - 1) {
                    LogUtil.addLog("中文不含小语种对应的资源 smallResBean = " + smallResBean);
                }
            }
            iterator.remove();
        }
    }

    /**
     * 查重，不会剔除，在控制台打印出现重复的resId，需要人力删除
     */
    public static void checkDuplication(List<List<ResBean>> multiResBeanList, String[] lans) {
        List<Integer> dulpiIndex = new ArrayList<>();
        List<Integer> dulp = new ArrayList<>();
        int index = -1;
        for (int i = 0; i < lans.length; i++) {
            if ("en".equals(lans[i])) {
                index = i;
            }
        }
        if (index < 0) {
            return;
        }
        //for (int i = 0; i < multiResBeanList.size(); i++) {
        List<ResBean> resBeanList = multiResBeanList.get(index);
        for (int j = 0; j < resBeanList.size(); j++) {
            if (dulpiIndex.contains(j)) {
                continue;
            }
            dulp.clear();
            ResBean resBean = resBeanList.get(j);
            try {
                int integer = Integer.parseInt(resBean.value);
                //LogUtil.addLog(TAG + " 出现数字, index = " + j + ", value = " + integer + ", 语言 = " + lans[index] + "\n");
                LogUtil.addLog("Util: 出现数字, index = " + j + ", value = " + integer + ", 语言 = " + lans[index] + "\n");
            } catch (Exception e) {
                //e.toString();
            }
            for (int k = j + 1; k < resBeanList.size(); k++) {
                ResBean dupliResBean = resBeanList.get(k);
                if (dupliResBean.value.trim().equals(resBean.value.trim())) {
                    if (!dulp.contains(j)) {
                        dulp.add(j);
                        dulpiIndex.contains(j);
                    }
                    dulp.add(k);
                    dulpiIndex.add(k);
                    //LogUtil.addLog(TAG + " 出现重复的内容, value = " + resBean.value);
                }
            }
            if (!dulp.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                sb.append("\n");
                for (int position : dulp) {
                    for (List<ResBean> stringResBeans : multiResBeanList) {
                        sb.append(stringResBeans.get(position).toString()).append("\n");
                    }
                }
                //LogUtil.addLog(TAG + " 出现重复的内容, index = " + dulp.toString() + " " + sb.toString());
                LogUtil.addLog("Util: 出现重复的内容, index = " + dulp.toString() + " " + sb.toString());
            }
        }
        //}
    }

    /**
     * @param resPath res资源路径
     */
    public static String[] getLanguages(String resPath) {
        File dir = new File(resPath);
        File[] files = dir.listFiles(file -> file.getName().startsWith("values") && new File(file, "strings.xml").exists());
        if (files == null) {
            LogUtil.addLog(TAG + "languageNames isEmpty!");
            return new String[0];
        }
        String[] languageNames = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            String name = file.getName();
            if (name.startsWith("values")) {
                if (name.length() > 6) {   //values-
                    languageNames[i] = name.substring(7);
                } else {
                    languageNames[i] = "en";
                }
            }
        }

        List<String> lans = new ArrayList<>();
        for (String languageName : languageNames) {
            if ("en".equals(languageName)) {
                lans.add(0, languageName);
            } else if ("zh".equals(languageName)) {
                lans.add(1, languageName);
            } else if ("zh-rTW".equals(languageName)) {
                lans.add(2, languageName);
            } else {
                lans.add(languageName);
            }
        }

        for (int i = 0; i < lans.size(); i++) {
            languageNames[i] = lans.get(i);
        }
        LogUtil.addLog(Util.TAG + " languageNames = " + Arrays.toString(languageNames));
        return languageNames;
    }

    /**
     * 查找重复的资源，该方法的意义是 查找source在sources里是否存在value一样，resId不一样的资源，并能在小语种smallResBeans里找到対应存在的resId，如果存在
     * 那么为小语种补一个resId = source.resId, value = 小语种.value的资源并返回。
     *
     * @param source        需要查找的某一条资源
     * @param sources       全部资源，
     * @param smallResBeans 小语种资源
     * @return StringResBean 可能是null
     */
    public static ResBean findDuplicateResBean(ResBean source, List<ResBean> sources, List<ResBean> smallResBeans) {
        List<ResBean> duplicateResBeans = new ArrayList<>();
        for (ResBean bean : sources) {
            if (source.value.equalsIgnoreCase(bean.value)) {
                duplicateResBeans.add(bean);
            }
        }
        if (duplicateResBeans.isEmpty()) {
            return null;
        }
        for (int i = 0; i < smallResBeans.size(); i++) {
            String resId = smallResBeans.get(i).resId;
            for (ResBean duplicateResBean : duplicateResBeans) {
                if (resId.equals(duplicateResBean.resId)) {
                    ResBean resBean = new ResBean();
                    resBean.resId = source.resId;
                    resBean.value = smallResBeans.get(i).value;
                    LogUtil.addLog(TAG + "findDuplicateResBean: 添加缺省的重复内容 resBean = " + resBean.toString() + ", i = " + i);
                    return resBean;
                }
            }
        }
        return null;
    }

    /**
     * 合并相同语言的资源，以第一个参数的顺序为主，当resId相同，value不同时，保留target1里的值不变
     *
     * @param target1 资料列表1
     * @param target2 资料列表2
     */
    public static List<ResBean> mergeResBeans(List<ResBean> target1, List<ResBean> target2) {
        return mergeResBeans(target1, target2, false, null);
    }

    /**
     * 合并相同语言的资源，以第一个参数的顺序为主
     *
     * @param target1          资料列表1
     * @param target2          资料列表2
     * @param isReplaceTarget1 当resId相同，value不同时，是否用target2的值替换target1的值
     * @param exceptIds        replace时，要排除exceptIds里的resId
     */
    public static List<ResBean> mergeResBeans(List<ResBean> target1, List<ResBean> target2, boolean isReplaceTarget1, List<String> exceptIds) {
        List<ResBean> result = new ArrayList<>();
        int count = 0;
        for (ResBean resBean1 : target1) {
            for (int j = 0; j < target2.size(); j++) {
                ResBean resBean2 = target2.get(j);
                if (resBean1.resId.equals(resBean2.resId)) {
                    if (!resBean1.value.equals(resBean2.value)) {
                        count++;
                        LogUtil.addLog(TAG + "mergeResBeans: value值不一样: " +
                                "\n       id = " + resBean1.resId +
                                "\n       resBean1.value = " + resBean1.value +
                                "\n       resBean2.value = " + resBean2.value);
                        if (isReplaceTarget1) {
                            if (exceptIds == null || !exceptIds.contains(resBean1.resId)) {
                                resBean1.value = resBean2.value;
                                LogUtil.addLog(TAG + "mergeResBeans: 将 resBean2.value 赋值给 resBean1.value value = " + resBean2.value);
                            } else {
                                LogUtil.addLog(TAG + "mergeResBeans: resId = " + resBean1.resId + " 资源不用替换，中文原文有变化，以项目中的为准");
                            }
                        }
                    }
                    target2.remove(j);
                    break;
                }
            }
            result.add(resBean1);
        }
        LogUtil.addLog(TAG + "mergeResBeans: resId相同，value值不同的数目 count = " + count);

        //补充target2里有target1里没有的
        /*for (int i = 0; i < target2.size(); i++) {
            StringResBean resBean2 = target2.get(i);
            for (int j = 0; j < target1.size(); j++) {
                StringResBean resBean1 = target1.get(j);
                if (resBean1.value.equalsIgnoreCase(resBean2.value)) {
                    LogUtil.addLog(TAG + "mergeResBeans: 找到value一样,id不一样的资源 resBean1 =" + resBean1 + ", resBean2 = " + resBean2);
                    result.add(resBean2);
                    break;
                }
                if (j == target1.size() - 1) {
                    count++;
                    LogUtil.addLog(TAG + "mergeResBeans: 增加新的资源 resBean2 = " + resBean2.toString());
                    result.add(resBean2);
                }
            }
        }*/
        result.addAll(target2);
        LogUtil.addLog(TAG + "mergeResBeans: 新增资源 target2 count = " + target2.size());
        return result;
    }

    public static List<ResBean> getStringResBeanList(List<List<String>> lists, int idIndex, int lanIndex) {
        List<ResBean> resBeans = new ArrayList<>();
        for (List<String> list : lists) {
            ResBean resBean = new ResBean();
            resBean.resId = list.get(idIndex);
            resBean.value = list.get(lanIndex);
            resBeans.add(resBean);
        }
        return resBeans;
    }

    /**
     * @param path res文件夹路径
     * @return values开头，并且存在 path/values/strings.xml 的文件夹集合
     */
    public static File[] listValuesDir(String path) {
        File file = new File(path);
        return file.listFiles((dir, name) -> name.startsWith("values") && new File(dir + "/" + name, "strings.xml").exists());
    }

}
