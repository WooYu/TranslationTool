package com.oyp.yy.xmlandexcel;

import com.oyp.yy.util.LogUtil;
import com.oyp.yy.util.StringUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by A18086 on 2018/5/30
 */
public class XmlExcelUtil {

    private static final String TAG = "XmlExcelUtil：";

    /**
     * 解析strings.xml文件
     *
     * @param filePath 文件路径 ..values/strings.xml ..value-zh/strings.xml
     * @return strings.xml里存储的语言信息
     */
    public static List<ResBean> readXml(String filePath) throws DocumentException {
        return readXml(filePath, false);
    }

    /**
     * 解析strings.xml文件
     *
     * @param filePath         文件路径 ..values/strings.xml ..value-zh/strings.xml
     * @param skipNotTranslate 是否跳过不需要翻译的资源
     * @return strings.xml里存储的语言信息
     */
    public static List<ResBean> readXml(String filePath, boolean skipNotTranslate) throws DocumentException {
        File file = new File(filePath);
        List<ResBean> resBeans;

        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(file);

        Element resources = document.getRootElement();
        String valuesName = file.getParentFile().getName();
        String lan = "";
        if (valuesName.startsWith("values")) {
            if (valuesName.equals("values")) {
                lan = "en";
            } else {
                lan = valuesName.substring("values-".length());
            }
        }
        resBeans = new ArrayList<>();
        A:
        for (Iterator i = resources.elementIterator(); i.hasNext(); ) {
            Element stringElement = (Element) i.next();
            ResBean resBean = new ResBean();
            resBean.lan = lan;
            resBean.resId = stringElement.attributeValue("name");
            if (stringElement.attributeCount() > 1) {
                resBean.attributes = new HashMap<>();
                for (int j = 0; j < stringElement.attributeCount(); j++) {
                    Attribute attribute = stringElement.attribute(j);
                    String name = attribute.getName();
                    if ("name".equals(name)) {
                        continue;
                    }
                    String value = attribute.getValue();
                    if (skipNotTranslate && "translatable".equals(name) && "false".equals(value)) {
                        continue A;
                    }
                    resBean.attributes.put(name, value);
                    //System.out.println(TAG + "attributeName = " + name + ", attributeValue = " + value);
                }
            }
            List elements = stringElement.elements();
            if (elements.isEmpty()) {
                resBean.value = Util.specialText(stringElement.getText())/*.trim()*/;
            } else {
                resBean.node = new ResBean.Node();
                resBean.value = obtainResValue((Element) elements.listIterator().next(), resBean.node);
            }
            resBeans.add(resBean);
            //System.out.println(TAG + " resBean = " + resBean);
        }
        return resBeans;
    }

    /**
     * 获取资源文件的value值
     *
     * @param element 解析的节点元素
     * @param node    自定义节点对象
     * @return resValue
     */
    private static String obtainResValue(Element element, ResBean.Node node) {
        node.name = element.getName();
        //System.out.println(TAG + "nodeName = " + node.name);
        if (element.attributeCount() > 0) {
            node.attributes = new HashMap<>();
            for (int i = 0; i < element.attributeCount(); i++) {
                Attribute attribute = element.attribute(i);
                String name = attribute.getName();
                String value = attribute.getValue();
                node.attributes.put(name, value);
                //System.out.println(TAG + "attributeName = " + name + ", attributeValue = " + value);
            }
        }
        List elements = element.elements();
        if (elements.isEmpty()) {
            return element.getText();
        } else {
            node.nextNode = new ResBean.Node();
            return obtainResValue((Element) elements.iterator().next(), node.nextNode);
        }
    }

    /**
     * 解析strings.xml文件
     *
     * @param resBeans    语言信息
     * @param xmlFilePath 生成的文件路径
     */
    public static void createXml(List<ResBean> resBeans, String xmlFilePath) throws IOException {
        createXml(resBeans, xmlFilePath, null);
    }

    /**
     * 解析strings.xml文件
     *
     * @param resBeans    语言信息
     * @param xmlFilePath 生成的文件路径
     * @param document    需要添加的注释
     */
    public static void createXml(List<ResBean> resBeans, String xmlFilePath, String document) throws IOException {
        File file = new File(xmlFilePath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));

        bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        bw.newLine();

        bw.write("<resources>");
        bw.newLine();

        if (document != null && document.length() > 0) {
            //添加注释
            bw.write("    <!--" + document + "-->");
            bw.newLine();
        }

        for (int i = 0; i < resBeans.size(); i++) {
            ResBean resBean = resBeans.get(i);
            if (resBean.resId == null || resBean.resId.length() == 0) {
                System.out.println(TAG + "createXml 缺少资源id resBean = " + resBean + ", row = " + i);
            }
            bw.write("    " + resBean.toString());
            bw.newLine();
            //System.out.println(TAG + " 写string节点 resId = " + resBean.resId + "，value = " + resBean.value);
        }

        bw.write("</resources>");

        bw.close();

    }

    /**
     * 解析Excel表格
     * 第一列：resId，第二列起为对应的语言资源
     *
     * @param excelPath 表格文件路径
     * @return 语言信息
     */
    public static List<List<ResBean>> readExcel(String excelPath) throws IOException {
        LogUtil.newLine();
        List<List<ResBean>> multiResBeanList = new ArrayList<>();
        List<String> lans = new ArrayList<>();
        File excelFile = new File(excelPath);
        FileInputStream fis = new FileInputStream(excelFile);

        Workbook workbook;
        if (excelFile.getName().endsWith(".xls")) {
            workbook = new HSSFWorkbook(fis);
        } else if (excelFile.getName().endsWith(".xlsx")) {
            workbook = new XSSFWorkbook(fis);
        } else {
            throw new RuntimeException(excelFile.getAbsolutePath() + "不是 excel 文件");
        }

        //获得第一张工作表
        Sheet sheet = workbook.getSheetAt(0);

        //从第0行开始
        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }

            //第一列是id，后边是对应的语言列
            Cell resIdCell = row.getCell(0); //取id
            if (resIdCell == null) {
                continue;
            }
            for (int j = 1; j < row.getLastCellNum(); j++) {  //取语言文本
                //读取单元格内容
                ResBean resBean = new ResBean();
                resBean.resId = resIdCell.getStringCellValue();
                Cell resBeanCell = row.getCell(j);
                if (resBeanCell != null) {
                    CellType cellType = resBeanCell.getCellTypeEnum();
                    if (cellType == CellType.NUMERIC) {
                        resBean.value = String.valueOf(resBeanCell.getNumericCellValue());
                        System.out.println(TAG + " NUMERIC value = " + resBean.value + " col = " + j + " row = " + i);
                    } else {
                        String cellValue = resBeanCell.getStringCellValue();
                        String newCellValue = Util.handleSpecialChar(cellValue);
                        if (!StringUtil.equals(cellValue, newCellValue)) {
                            LogUtil.addLog(TAG + " 含特殊字符： ");
                            LogUtil.addLog("        cellValue = " + cellValue);
                            LogUtil.addLog("     newCellValue = " + newCellValue);
                        }
                        resBean.value = newCellValue;
                    }
                }

                List<ResBean> resBeans;
                if (j - 1 >= multiResBeanList.size()) {
                    resBeans = new ArrayList<>();
                    multiResBeanList.add(resBeans);
                    if (i == 0) {
                        lans.add(resBean.value.toLowerCase());
                    }
                } else {
                    resBeans = multiResBeanList.get(j - 1);
                }

                resBean.lan = lans.get(j - 1);
                if (resBean.value != null && resBean.value.length() > 0) {
                    resBeans.add(resBean);
                    //System.out.println(TAG + " 单元格信息 resBean = " + resBean);
                } else {
                    System.out.println(TAG + " 单元格信息 resBean = " + resBean);
                }
            }
        }

        fis.close();
        workbook.close();
        LogUtil.newLine();
        return multiResBeanList;
    }

    /**
     * 创建excel表格
     *
     * @param multiResBeans 多语言文本信息
     * @param excelPath     excel 表格路径
     */
    public static void createExcel(List<List<ResBean>> multiResBeans, String excelPath) throws IOException {

        File excelFile = new File(excelPath);
        if (!excelFile.getParentFile().exists()) {
            excelFile.getParentFile().mkdirs();
        }
        //创建Excel文件
        Workbook workbook;
        if (excelFile.getName().endsWith(".xls")) {
            workbook = new HSSFWorkbook();
        } else if (excelFile.getName().endsWith(".xlsx")) {
            workbook = new XSSFWorkbook();
        } else {
            throw new RuntimeException(excelPath + " is not a excel!");
        }
        //创建一个表格
        Sheet sheet = workbook.createSheet("sheet1");

        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setWrapText(true);    //自动换行
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);  // 上下居中

        Font font = workbook.createFont();
        font.setFontName("微软雅黑");
        font.setFontHeightInPoints((short) 12);
        cellStyle.setFont(font);

        for (int i = 0; i < multiResBeans.size() + 1; i++) {
            if (i == 0) {
                sheet.setColumnWidth(i, 8000);
            } else {
                sheet.setColumnWidth(i, 15000);
            }
        }

        //添加表头行
        Row titleRow = sheet.createRow(0);
        //第0列，第0行，resId
        Cell resIdCell = titleRow.createCell(0);
        resIdCell.setCellValue("resId");
        resIdCell.setCellStyle(cellStyle);
        for (int i = 0; i < multiResBeans.size(); i++) {
            Cell lanResCell = titleRow.createCell(i + 1);
            List<ResBean> resBeans = multiResBeans.get(i);
            if (resBeans.size() > 0) {
                lanResCell.setCellValue(resBeans.get(0).lan);
                lanResCell.setCellStyle(cellStyle);
            }
        }

        //表格正式内容
        int row = 1;
        int size = multiResBeans.get(0).size();
        for (int i = 0; i < size; i++) {
            Row sheetRow = sheet.createRow(row);
            String resId = null;
            for (int j = 0; j < multiResBeans.size(); j++) {
                List<ResBean> resBeans = multiResBeans.get(j);
                ResBean resBean = resBeans.get(i);
                if (j == 0) {
                    resId = resBean.resId;
                    Cell cell = sheetRow.createCell(0);
                    cell.setCellValue(resBean.resId);
                    cell.setCellStyle(cellStyle);
                    //System.out.println(TAG + " 添加第0列第" + row + "行，resId = " + resBean.resId);
                } else {
                    if (!resId.equals(resBean.resId)) {
                        throw new RuntimeException("id is not same, j = " + j + ", resBean1 = " + multiResBeans.get(0).get(i) + ", resBean2 = " + resBean);
                    }
                }
                Cell cell = sheetRow.createCell(j + 1);
                cell.setCellValue(resBean.value);
                cell.setCellStyle(cellStyle);
                //System.out.println(TAG + " 添加第" + (j + 1) + "列第" + row + "行，内容 = " + Util.specialText(resBean.value));
            }
            row++;
        }
        //  写入数据并关闭文件
        workbook.write(new FileOutputStream(excelFile));
        workbook.close();
    }

}
