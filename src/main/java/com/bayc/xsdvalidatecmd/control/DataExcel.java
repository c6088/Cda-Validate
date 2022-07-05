package com.bayc.xsdvalidatecmd.control;

import com.bayc.xsdvalidatecmd.enums.EnumCodeName;
import com.bayc.xsdvalidatecmd.model.CdaNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * DataExcel类， 处理Excel设置数据， Excel设置数据以静态对象保存，作为缓存。
 */
public class DataExcel {

    private Log log = LogFactory.getLog(DataExcel.class);
    private List<String> errorMessage = new ArrayList<>();

    /**
     * 执行时的错误信息列表
     */
    public List<String> getErrorMessage() {
        return errorMessage;
    }

    private static boolean isNodeLoaded = false;
    private static boolean isDeLoaded = false;
    private static boolean isDictLoaded = false;
    /**
     * 节点数据定义
     */
    public static List<CdaNode> listCdaDefine = new ArrayList<>();
    /**
     * 数据元的值域定义
     */
    public static HashMap<String, String> mapDeDomain = new HashMap<String, String>();
    private XSSFWorkbook sheets = null;
    /**
     * 字典数据定义
     */
    public static HashMap<String, HashMap<String, String>> mapDicts = new HashMap<>();

    /**
     * 构造函数
     */
    public DataExcel() {
    }

    /**
     * 析构函数
     */
    public void finalize() {
        close();
        log = null;
        if (errorMessage != null) {
            errorMessage.clear();
            errorMessage = null;
        }
    }

    private void SetErrorInfo(String errorInfo) {
        errorMessage.add(errorInfo);
        log.info(errorInfo);
    }

    /**
     * 传入excelPath，为空默认为config/数据集-2016版.xlsx。打开Excel慢，不能多次打开。
     */
    public boolean open(String excelPath) {
        boolean isOk = false;
        if (excelPath.isEmpty()) excelPath = "config/数据集-2016版.xlsx";
        try {
            errorMessage.clear();
            FileInputStream inputStream = new FileInputStream(excelPath);
            sheets = new XSSFWorkbook(inputStream);//String excelPath
            isDeLoaded = false;
            isNodeLoaded = false;
            isDictLoaded = false;
            isOk = true;
        } catch (Exception ex) {
            String str = "读" + excelPath + "错误:" + ex.getMessage();
            errorMessage.add(str);
            log.info(str);
        }
        return isOk;
    }


    /**
     * 关闭Excel，再读Excel时，需要重新打开。
     */
    public void close() {
        if (sheets != null) {
            try {
                sheets.close();
            } catch (IOException ex) {
            }
            sheets = null;
        }
    }

    /**
     * 获取全部的CDA操作定义list。
     *
     * @return List<CdaNode>
     */
    public List<CdaNode> getListCdaDefine() {
        if (!isNodeLoaded) {
            errorMessage.clear();
            try {
                loadCdaDict(sheets);
                isNodeLoaded = true;
            } catch (Exception ex) {
                String str = "getListCdaDefine错误:" + ex.getMessage();
                errorMessage.add(str);
                log.info(str);
            }
        }
        return listCdaDefine;
    }

    /**
     * 获取某个CDA代码的操作定义list。请不要每次都调这个函数，多次用时，应将中间结果保存下来使用。
     *
     * @param cdaCode CDA代码
     * @return List<CdaNode>, 如果代码为空，则返回null
     */
    public List<CdaNode> getListCdaDefine(String cdaCode) {
        if (cdaCode == null || cdaCode.isEmpty()) {
            SetErrorInfo("cdaCode不能为空");
            return null;
        }
        getListCdaDefine();

        List<CdaNode> list = new ArrayList<>();
        for (CdaNode node : listCdaDefine) {
            if (node.getCdaCode().compareTo(cdaCode) == 0) {
                list.add(node);
            }
        }
        return list;
    }

    /**
     * 获取元素值域定义HashMap
     *
     * @return HashMap<String, String>
     */
    public HashMap<String, String> getDeDomain() {
        if (!isDeLoaded) {
            errorMessage.clear();
            try {
                loadDeDomain(sheets);
                isDeLoaded = true;
            } catch (Exception ex) {
                String str = "读getDeDomain错误:" + ex.getMessage();
                errorMessage.add(str);
                log.info(str);
            }
        }
        return mapDeDomain;
    }

    /**
     * 获取字典数据mapDicts
     *
     * @return HashMap<String, String>
     */
    public HashMap<String, HashMap<String, String>> getMapDict() {
        if (!isDictLoaded) {
            errorMessage.clear();
            String excelPath = "config/CDA字典信息.xlsx";
            XSSFWorkbook sheets = null;
            try {
                FileInputStream inputStream = new FileInputStream(excelPath);
                sheets = new XSSFWorkbook(inputStream);  //String excelPath
                loadMapDicts(sheets);
                isDictLoaded = true;
            } catch (Exception ex) {
                String str = "读getMapDicts错误:" + ex.getMessage();
                errorMessage.add(str);
                log.info(str);
            } finally {
                if (sheets != null) {
                    try {
                        sheets.close();
                    } catch (Exception e) {
                    }
                }
            }
        }
        return mapDicts;
    }

    /**
     * 根据字典名称单个字典数据
     *
     * @param dictName 字典名称
     * @return HashMap<String, String>, 没有对应名称的字典时，返回null
     */
    public HashMap<String, String> getMapDict(String dictName) {
        getMapDict();
        if (!mapDicts.containsKey(dictName)) {
            String errorInfo = String.format("没有名称为%s的字典。", dictName);
            SetErrorInfo(errorInfo);
            return null;
        }
        return mapDicts.get(dictName);
    }

    /**
     * 读Excel单元格中的字符串
     *
     * @param cell XSSFCell
     * @return String
     */
    private String GetCellText(XSSFCell cell) {
        if (cell == null) return "";
        return GCLib.CStr(cell.toString()).trim();
    }

    /**
     * 从Excel中读取CDA操作定义list。
     *
     * @param sheets XSSFWorkbook
     */
    private void loadCdaDict(XSSFWorkbook sheets) {
        listCdaDefine.clear();
        XSSFSheet sheet = sheets.getSheet("校验设置");
        int lastRowNum = sheet.getLastRowNum();
        XSSFRow row;
        CdaNode node;
        errorMessage.clear();
        for (int i = 1; i < lastRowNum; i++) {
            int col;
            String str;
            row = sheet.getRow(i);
            if (row == null) continue;

            node = new CdaNode();
            col = 0;
            try {
                col++;
                str = GetCellText(row.getCell(0));
                node.setTableName(str);
                col++;
                str = GetCellText(row.getCell(1));
                node.setFieldName(str);
                col++;
                str = GetCellText(row.getCell(2));
                node.setFieldDescription(str);
                col++;
                str = GetCellText(row.getCell(3));
                node.setCdaCode(str);
                col++;
                str = GetCellText(row.getCell(4));
                node.setDataType(str);
                col++;
                str = GetCellText(row.getCell(5));
                node.setConstraint(str);
                col++;
                str = GetCellText(row.getCell(6));
                node.setMetaCode(str);
                col++;
                str = GetCellText(row.getCell(7));
                node.setCodomain(str);
                col++;
                str = GetCellText(row.getCell(8));
                node.setCdaDictName(str);
                col++;
                str = GetCellText(row.getCell(9)).toUpperCase();
                switch (str) {
                    case "CODE":
                        node.setEnumCodeName(EnumCodeName.Code);
                        break;
                    case "NAME":
                        node.setEnumCodeName(EnumCodeName.Name);
                        break;
                }
                col++;
                str = GetCellText(row.getCell(10));
                node.setNameCodeField(str);
                col++;
                str = GetCellText(row.getCell(11));
                node.setNodePath(str);
                col++;
                str = GetCellText(row.getCell(12));
                int v = GCLib.CInt(str);
                node.setIsVerified(v);
                listCdaDefine.add(node);
            } catch (Exception ex) {
                str = String.format("Excel第%d行%d列错误：%s", i, col, ex.getMessage());
                errorMessage.add(str);
                log.info(str);
                ex.printStackTrace();
            }
        }
    }

    /**
     * 从Excel中读取元素值域定义。
     *
     * @param sheets XSSFWorkbook
     */
    private void loadDeDomain(XSSFWorkbook sheets) {
        mapDeDomain.clear();
        XSSFSheet sheet = sheets.getSheet("数据表详细字段信息");
        int lastRowNum = sheet.getLastRowNum();
        XSSFRow row;
        String eleCode, domainValue;
        errorMessage.clear();
        for (int i = 1; i < lastRowNum; i++) {
            int col = 0;
            String str;
            row = sheet.getRow(i);
            if (row == null) continue;

            try {
                col = 1;
                eleCode = GetCellText(row.getCell(col));
                if (eleCode.isEmpty()) continue;
                col = 5;
                domainValue = GetCellText(row.getCell(col));
                if (domainValue.isEmpty()) continue;

                if (!mapDeDomain.containsKey(eleCode)) {
                    mapDeDomain.put(eleCode, domainValue);
                }
            } catch (Exception ex) {
                str = String.format("第%d行%d错误：%s", i, col, ex.getMessage());
                errorMessage.add(str);
                log.info(str);
                ex.printStackTrace();
            }
        }
    }

    /**
     * 从Excel中读取字典设置
     *
     * @param sheets XSSFWorkbook
     */
    private void loadMapDicts(XSSFWorkbook sheets) {
        mapDicts.clear();
        XSSFSheet sheet = sheets.getSheet("CDA字典");
        int lastRowNum = sheet.getLastRowNum();
        XSSFRow row;
        String dictName, key, val;
        errorMessage.clear();
        for (int i = 1; i < lastRowNum; i++) {
            int col = 0;
            String str;
            row = sheet.getRow(i);
            if (row == null) continue;
            HashMap<String, String> hsMap;
            try {
                col = 0;
                dictName = GetCellText(row.getCell(col));
                if (dictName.isEmpty()) continue;
                col = 2;
                key = GetCellText(row.getCell(col));
                if (key.isEmpty()) continue;
                col = 3;
                val = GetCellText(row.getCell(col));
                if (val.isEmpty()) continue;

                if (!mapDicts.containsKey(dictName)) {
                    mapDicts.put(dictName, new HashMap<String, String>());
                }
                hsMap = mapDicts.get(dictName);
                hsMap.put(key, val);
            } catch (Exception ex) {
                str = String.format("第%d行%d错误：%s", i, col, ex.getMessage());
                errorMessage.add(str);
                log.info(str);
                ex.printStackTrace();
            }
        }
    }


    /**
     * 通过字典名称和键，取对应值
     *
     * @param dictName 字典名称
     * @param key      字典键
     * @return boolean 字典的值
     */
    public String getDictValue(String dictName, String key) {
        if (!mapDicts.containsKey(dictName)) return "";
        HashMap<String, String> hsMap = mapDicts.get(dictName);
        if (!hsMap.containsKey(key)) return "";
        return hsMap.get(key);
    }

    /**
     * 通过字典名称和键，取对应值
     *
     * @param dictName 字典名称
     * @param key      字典键
     * @return true存在 false不存在
     */
    public boolean ExistsDictKey(String dictName, String key) {
        if (!mapDicts.containsKey(dictName)) return false;
        HashMap<String, String> hsMap = mapDicts.get(dictName);
        return hsMap.containsKey(key);
    }

    /**
     * 通过字典名称和键，取对应值
     *
     * @param dictName 字典名称
     * @param val      字典值
     * @return true存在 false不存在
     */
    public boolean ExistsDictValue(String dictName, String val) {
        if (dictName.isEmpty() || val.isEmpty()) return false;
        if (!mapDicts.containsKey(dictName)) return false;
        boolean isOk = false;
        HashMap<String, String> hsMap = mapDicts.get(dictName);
        for (String v : hsMap.values()) {
            if (val.compareTo(v) == 0) {
                isOk = true;
                break;
            }
        }
        return isOk;
    }
}
