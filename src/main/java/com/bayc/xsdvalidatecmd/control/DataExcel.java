package com.bayc.xsdvalidatecmd.control;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.bayc.xsdvalidatecmd.enums.EnumCodeName;
import com.bayc.xsdvalidatecmd.model.CdaNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
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
     *
     * @return 错误信息列表
     */
    public List<String> getErrorMessage() {
        return errorMessage;
    }

    private static boolean isNodeLoaded = false;
    private static boolean isDeLoaded = false;
    private static boolean isDictLoaded = false;
    private static boolean isDictVersionLoaded = false;
    /**
     * 节点数据定义, 节点代码对应节点定义列表
     */
    public static HashMap<String, List<CdaNode>> mapListCdaDefine = new HashMap<>();
    /**
     * 数据元的值域定义
     */
    public static HashMap<String, String> mapDeDomain = new HashMap<String, String>();
    private XSSFWorkbook sheetsDefine = null;
    private XSSFWorkbook sheetsDict = null;
    /**
     * 字典数据定义
     */
    public static HashMap<String, HashMap<String, String>> mapDicts = new HashMap<>();
    /**
     * 标准字典所用的版本
     */
    public static HashMap<String, String> mapDictVersion = new HashMap<String, String>();

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

    private void setErrorInfo(String errorInfo) {
        errorMessage.add(errorInfo);
        log.info(errorInfo);
    }

    /**
     * 将数据写入json文件
     *
     * @return true成功, false失败
     */
    public boolean dataWriteToJsonFile() {
        String filePathDict, filePathVer, filePathNode, filePathDomain, jsonStr;
        String path = CdaConfig.configFilePath;
        filePathDict = path + "CdaDict.json";
        filePathVer = path + "CdaDictVersion.json";
        filePathNode = path + "CdaNodeDefine.json";
        filePathDomain = path + "CdaDomain.json";
        jsonStr = this.objectToJsonStr(mapDicts);
        GCLib.writeFileAllText(filePathDict, jsonStr);
        jsonStr = this.objectToJsonStr(mapDictVersion);
        GCLib.writeFileAllText(filePathVer, jsonStr);
        jsonStr = this.objectToJsonStr(mapListCdaDefine);
        GCLib.writeFileAllText(filePathNode, jsonStr);
        jsonStr = this.objectToJsonStr(mapDeDomain);
        GCLib.writeFileAllText(filePathDomain, jsonStr);
        return true;
    }

    /**
     * 从JSON文件中读设置数据
     *
     * @return true成功，false失败
     */
    public boolean dataLoadFromJsonFile() {
        File fileDict, fileNode, fileDomain, fileDictVer;
        String filePathDict, filePathVer, filePathNode, filePathDomain, jsonStr;
        String path = CdaConfig.configFilePath;
        filePathDict = path + "CdaDict.json";
        filePathVer = path + "CdaDictVersion.json";
        filePathNode = path + "CdaNodeDefine.json";
        filePathDomain = path + "CdaDomain.json";

        fileDict = new File(filePathDict);
        fileDictVer = new File(filePathVer);
        fileNode = new File(filePathNode);
        fileDomain = new File(filePathDomain);
        boolean isOk = false;
        if (fileDict.exists() && fileDictVer.exists() && fileNode.exists() && fileDomain.exists()) {
            jsonStr = GCLib.readFileALLText(filePathDict);
            mapDicts = this.jsonStrToHashMap2(jsonStr);
            jsonStr = GCLib.readFileALLText(filePathVer);
            mapDictVersion = this.jsonStrToHashMap(jsonStr);
            jsonStr = GCLib.readFileALLText(filePathNode);
            mapListCdaDefine = this.<CdaNode>jsonStrToHashMapList(jsonStr);
            jsonStr = GCLib.readFileALLText(filePathDomain);
            mapDeDomain = this.jsonStrToHashMap(jsonStr);
            isDictLoaded = true;
            isDictVersionLoaded = true;
            isNodeLoaded = true;
            isDeLoaded = true;
            isOk = true;
        } else {
            String errorInfo = String.format("以下四个文件，至少有一个不存在。\n%s\n%s\n%s\n%s", filePathDict, fileDictVer, filePathNode, filePathDomain);
            setErrorInfo(errorInfo);
        }
        return isOk;
    }

    /**
     * 将JSON字符串解析为List
     *
     * @param jsonStr json字符串
     * @param obj     Class&lt;T&gt;对象
     * @param <T>     泛型类
     * @return List&lt;T&gt;
     */
    public <T> List<T> jsonStrToList(String jsonStr, Class<T> obj) {
        return JSONArray.parseArray(jsonStr, obj);
    }

    /**
     * 将 JsonStr解析为HashMap&lt;String,HashMap&lt;String,String&gt;&gt;
     *
     * @param jsonStr jsonStr
     * @return HashMap&lt;String, HashMap&lt;String, String&gt;&gt;
     */
    public HashMap<String, HashMap<String, String>> jsonStrToHashMap2(String jsonStr) {
        return (HashMap<String, HashMap<String, String>>)
                JSONObject.parseObject(jsonStr, new TypeReference<HashMap<String, HashMap<String, String>>>() {
                });
    }

    /**
     * 将 JsonStr解析为HashMap&lt;String,HashMap&lt;String,String&gt;&gt;
     *
     * @param jsonStr jsonStr
     * @param <T>     泛型类
     * @return HashMap&lt;String, HashMap&lt;String, String&gt;&gt;
     */
    public <T> HashMap<String, List<CdaNode>> jsonStrToHashMapList(String jsonStr) {
        return (HashMap<String, List<CdaNode>>) JSONObject.parseObject(jsonStr, new TypeReference<HashMap<String, List<CdaNode>>>() {
        });
    }

    /**
     * 将json字符串转化为HashMap字典
     *
     * @param jsonStr json字符串
     * @return HashMap字典
     */
    public HashMap<String, String> jsonStrToHashMap(String jsonStr) {
        return (HashMap<String, String>) JSONObject.parseObject(jsonStr, new TypeReference<HashMap<String, String>>() {
        });
    }

    /**
     * 将对象JSON序列化
     *
     * @param obj Object
     * @return String
     */
    public String objectToJsonStr(Object obj) {
        return JSONObject.toJSONString(obj);
    }

    /**
     * 将JSON字符串解析为对象T
     *
     * @param jsonStr jsonStr
     * @param obj     泛型对象
     * @param <T>     泛型类
     * @return 泛型对象
     */
    public <T> T jsonStrToObject(String jsonStr, Class<T> obj) {
        return JSONObject.parseObject(jsonStr, obj);
    }

    /**
     * 传入excelPath，为空默认为config/数据集-2016版.xlsx。打开Excel慢，不能多次打开。
     *
     * @return true打开成功，false打开失败
     */
    public boolean open() {
        boolean isOk = false;
        String excelPath = "";
        errorMessage.clear();
        try {
            excelPath = CdaConfig.cdaDatasetExcel;
            sheetsDefine = openSheets(excelPath);
            excelPath = CdaConfig.cdaDictionaryExcel;
            sheetsDict = openSheets(excelPath);
            isDeLoaded = false;
            isNodeLoaded = false;
            isDictLoaded = false;
            isDictVersionLoaded = false;
            isOk = true;
        } catch (Exception ex) {
            String errorInfo = "读" + excelPath + "文件错误:" + ex.getMessage();
            setErrorInfo(errorInfo);
        }
        return isOk;
    }

    /**
     * 关闭Excel，再读Excel时，需要重新打开。
     */
    public void close() {
        closeSheets(sheetsDefine);
        closeSheets(sheetsDict);
    }

    private XSSFWorkbook openSheets(String excelPath) {
        XSSFWorkbook sheets = null;
        try {
            FileInputStream inputStream = new FileInputStream(excelPath);
            sheets = new XSSFWorkbook(inputStream);
        } catch (Exception ex) {
            String errorInfo = "读" + excelPath + "错误:" + ex.getMessage();
            setErrorInfo(errorInfo);
        }
        return sheets;
    }

    private void closeSheets(XSSFWorkbook sheets) {
        if (sheetsDefine != null) {
            try {
                sheetsDefine.close();
            } catch (IOException ex) {
            }
            sheetsDefine = null;
        }
    }

    /**
     * 获取全部的CDA操作定义list。
     *
     * @return List&lt;CdaNode&gt;
     */
    public HashMap<String, List<CdaNode>> getListCdaDefine() {
        if (!isNodeLoaded) {
            try {
                loadListCdaDefine(sheetsDefine);
                isNodeLoaded = true;
            } catch (Exception ex) {
                String errorInfo = "数据集-2016版.xlsx中的检验设置错误:" + ex.getMessage();
                setErrorInfo(errorInfo);
            }
        }
        return mapListCdaDefine;
    }

    /**
     * 获取某个CDA代码的操作定义list。请不要每次都调这个函数，多次用时，应将中间结果保存下来使用。
     *
     * @param cdaCode CDA代码
     * @return List&lt;CdaNode&gt;, 如果代码为空，则返回null
     */
    public List<CdaNode> getListCdaDefine(String cdaCode) {
        if (cdaCode == null || cdaCode.isEmpty()) {
            setErrorInfo("cdaCode不能为空");
            return null;
        }
        getListCdaDefine();

        if (!mapListCdaDefine.containsKey(cdaCode)) {
            setErrorInfo(String.format("没有cdaCode(%s)的节点定义。", cdaCode));
            return null;
        }
        return mapListCdaDefine.get(cdaCode);
    }

    /**
     * 获取元素值域定义HashMap
     *
     * @return HashMap&lt;String, String&gt;
     */
    public HashMap<String, String> getDeDomain() {
        if (!isDeLoaded) {
            try {
                loadDeDomain(sheetsDefine);
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
     * @return HashMap&lt;String, String&gt;
     */
    public HashMap<String, HashMap<String, String>> getMapDict() {
        if (!isDictLoaded) {
            try {
                loadMapDicts(sheetsDict);
                isDictLoaded = true;
            } catch (Exception ex) {
                String errorInfo = "读CDA字典信息.xlsx中CDA字典数据错误:" + ex.getMessage();
                setErrorInfo(errorInfo);
            }
        }
        return mapDicts;
    }

    /**
     * 根据字典名称单个字典数据
     *
     * @param dictName 字典名称
     * @return HashMap&lt;String, String&gt;, 没有对应名称的字典时，返回null
     */
    public HashMap<String, String> getMapDict(String dictName) {
        getMapDict();
        if (!mapDicts.containsKey(dictName)) {
            String errorInfo = String.format("没有名称为%s的字典。", dictName);
            setErrorInfo(errorInfo);
            return null;
        }
        return mapDicts.get(dictName);
    }

    /**
     * 获取字典数据mapDicts
     *
     * @return HashMap&lt;String, String&gt;
     */
    public HashMap<String, String> getMapDictVersion() {
        if (!isDictVersionLoaded) {
            try {
                loadMapDictVersion(sheetsDict);
                isDictVersionLoaded = true;
            } catch (Exception ex) {
                String errorInfo = "读CDA字典信息.xlsx中CDA字典版本错误:" + ex.getMessage();
                setErrorInfo(errorInfo);
            }
        }
        return mapDictVersion;
    }

    /**
     * 读Excel单元格中的字符串
     *
     * @param cell XSSFCell
     * @return String
     */
    private String getCellText(XSSFCell cell) {
        if (cell == null) return "";
        return GCLib.CStr(cell.toString()).trim();
    }

    /**
     * 从Excel中读取CDA操作定义list。
     *
     * @param sheets XSSFWorkbook
     */
    private void loadListCdaDefine(XSSFWorkbook sheets) {
        mapListCdaDefine.clear();
        XSSFSheet sheet = sheets.getSheet("校验设置");
        int lastRowNum = sheet.getLastRowNum();
        XSSFRow row;
        CdaNode node;
        errorMessage.clear();
        for (int i = 1; i < lastRowNum; i++) {
            int col;
            String str, errorInfo, key;
            row = sheet.getRow(i);
            if (row == null) continue;

            node = new CdaNode();
            col = 0;
            try {
                col++;
                str = getCellText(row.getCell(0));
                node.setTableName(str);
                col++;
                str = getCellText(row.getCell(1));
                node.setFieldName(str);
                col++;
                str = getCellText(row.getCell(2));
                node.setFieldDescription(str);
                col++;
                str = getCellText(row.getCell(3));
                node.setCdaCode(str);
                col++;
                str = getCellText(row.getCell(4));
                node.setDataType(str);
                col++;
                str = getCellText(row.getCell(5));
                node.setConstraint(str);
                col++;
                str = getCellText(row.getCell(6));
                node.setMetaCode(str);
                col++;
                str = getCellText(row.getCell(7));
                node.setCodomain(str);
                col++;
                str = getCellText(row.getCell(8));
                node.setCdaDictName(str);
                col++;
                str = getCellText(row.getCell(9)).toUpperCase();
                switch (str) {
                    case "CODE":
                        node.setEnumCodeName(EnumCodeName.Code);
                        break;
                    case "NAME":
                        node.setEnumCodeName(EnumCodeName.Name);
                        break;
                }
                col++;
                str = getCellText(row.getCell(10));
                node.setNameCodeField(str);
                col++;
                str = getCellText(row.getCell(11));
                node.setNodePath(str);
                col++;
                str = getCellText(row.getCell(12));
                int v = GCLib.CInt(str);
                node.setIsVerified(v);
                key = node.getCdaCode();
                if (!mapListCdaDefine.containsKey(key)) {
                    mapListCdaDefine.put(key, new ArrayList<>());
                }
                mapListCdaDefine.get(key).add(node);
            } catch (Exception ex) {
                errorInfo = String.format("Excel第%d行%d列错误：%s", i, col, ex.getMessage());
                setErrorInfo(errorInfo);
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
        for (int i = 1; i < lastRowNum; i++) {
            int col = 0;
            String str, errorInfo;
            row = sheet.getRow(i);
            if (row == null) continue;

            try {
                col = 1;
                eleCode = getCellText(row.getCell(col));
                if (eleCode.isEmpty()) continue;
                col = 5;
                domainValue = getCellText(row.getCell(col));
                if (domainValue.isEmpty()) continue;

                if (!mapDeDomain.containsKey(eleCode)) {
                    mapDeDomain.put(eleCode, domainValue);
                }
            } catch (Exception ex) {
                errorInfo = String.format("数据集-2016版.xlsx中数据表详细字段信息中第%d行%d错误：%s", i, col, ex.getMessage());
                setErrorInfo(errorInfo);
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
            String errorInfo;
            row = sheet.getRow(i);
            if (row == null) continue;
            HashMap<String, String> hsMap;
            try {
                col = 0;
                dictName = getCellText(row.getCell(col));
                if (dictName.isEmpty()) continue;
                col = 2;
                key = getCellText(row.getCell(col));
                if (key.isEmpty()) continue;
                col = 3;
                val = getCellText(row.getCell(col));
                if (val.isEmpty()) continue;

                if (!mapDicts.containsKey(dictName)) {
                    mapDicts.put(dictName, new HashMap<String, String>());
                }
                hsMap = mapDicts.get(dictName);
                hsMap.put(key, val);
            } catch (Exception ex) {
                errorInfo = String.format("第%d行%d错误：%s", i, col, ex.getMessage());
                setErrorInfo(errorInfo);
                ex.printStackTrace();
            }
        }
    }

    /**
     * 从Excel中读取元素值域定义。
     *
     * @param sheets XSSFWorkbook
     */
    private void loadMapDictVersion(XSSFWorkbook sheets) {
        mapDictVersion.clear();
        XSSFSheet sheet = sheets.getSheet("CDA字典版本");
        int lastRowNum = sheet.getLastRowNum();
        XSSFRow row;

        for (int i = 1; i < lastRowNum; i++) {
            int col = 0;
            String dictName, dicVersionName, errorInfo;
            row = sheet.getRow(i);
            if (row == null) continue;

            try {
                col = 0;
                dictName = getCellText(row.getCell(col));
                if (dictName.isEmpty()) continue;
                col = 1;
                dicVersionName = getCellText(row.getCell(col));
                if (dicVersionName.isEmpty()) continue;
                mapDictVersion.put(dictName, dicVersionName);
            } catch (Exception ex) {
                errorInfo = String.format("CDA字典信息.xlsx中CDA字典版本中第%d行%d错误：%s", i, col, ex.getMessage());
                setErrorInfo(errorInfo);
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
