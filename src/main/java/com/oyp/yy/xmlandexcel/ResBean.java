package com.oyp.yy.xmlandexcel;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Created by A18086 on 2018/5/29
 * strings.xml里的语言文本数据
 */
public class ResBean {

    public String lan = "";      //语言名称缩写
    public String resId;    //资源id
    public String value;    //资源值
    public Map<String, String> attributes; // string 节点除 resId 之外的属性
    public Node node;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResBean)) return false;

        ResBean that = (ResBean) o;

        return Objects.equals(resId, that.resId);
    }

    /**
     * 是否是需要翻译的资源，translate=false 或者 值引用了另一个资源，不需要翻译
     */
    public boolean isNeedTranslate() {
        if (attributes != null && "false".equals(attributes.get("translatable"))) {
            return false;
        }
        return value == null || !value.startsWith("@string/");
    }

    @Override
    public int hashCode() {
        return resId != null ? resId.hashCode() : 0;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("<string name=\"").append(resId).append("\"");
        if (attributes != null) {
            Set<Map.Entry<String, String>> entries = attributes.entrySet();
            for (Map.Entry<String, String> attribute : entries) {
                String key = attribute.getKey();
                String value = attribute.getValue();
                builder.append(" ").append(key).append("=\"").append(value).append("\"");
            }
        }
        builder.append(">");
        if (node != null) {
            node.value = value;
            builder.append(node.toString());
        } else {
            builder.append(value);
        }
        builder.append("</string>");

        /*if (node != null || attributes != null) {
            System.out.println(builder.toString());
        }*/

        return builder.toString();

        //return "<string name=\"" + resId + "\">" + value + "</string>";
    }

    public static class Node {

        /**
         * 当前节点名称
         */
        public String name;
        /**
         * 下一级节点
         */
        public Node nextNode;
        /**
         * 当前节点属性值
         */
        public Map<String, String> attributes; //当前节点属性值
        public String value;

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("<").append(name);
            if (attributes != null) {
                Set<Map.Entry<String, String>> entries = attributes.entrySet();
                for (Map.Entry<String, String> attribute : entries) {
                    String key = attribute.getKey();
                    String value = attribute.getValue();
                    builder.append(" ").append(key).append("=\"").append(value).append("\"");
                }
            }
            builder.append(">");
            if (nextNode != null) {
                nextNode.value = value;
                builder.append(nextNode.toString());
            } else {
                builder.append(value);
            }
            builder.append("</").append(name).append(">");
            return builder.toString();
        }
    }
}
