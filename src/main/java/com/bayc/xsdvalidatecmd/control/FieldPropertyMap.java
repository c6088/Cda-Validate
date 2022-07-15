package com.bayc.xsdvalidatecmd.control;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 数据库字段与实体属性字段的映射类
 */
public class FieldPropertyMap {
    private Log log = LogFactory.getLog(DataExcel.class);
    private List<String> errorMessage = new ArrayList<>();
    private static HashMap<String, HashMap<String, String>> mapFieldProperty = new HashMap<>();

    public List<String> getErrorMessage() {
        return errorMessage;
    }

    /**
     * HashMap&lt;字典名称, HashMap&lt;数据表字段, 实体字段&gt;&gt;
     *
     * @return HashMap&lt;字典名称, HashMap&lt;数据表字段, 实体字段&gt;&gt;
     */
    public HashMap<String, HashMap<String, String>> getMapFieldProperty() {
        return mapFieldProperty;
    }

    /**
     * 构造函数
     */
    public FieldPropertyMap() {
    }

    /**
     * 析构函数
     */
    public void finalize() {
        log = null;
        if (errorMessage != null) {
            errorMessage.clear();
            errorMessage = null;
        }
    }

    /**
     * HashMap&lt;数据表字段, 实体字段&gt;
     *
     * @param cdaCode CDA代码
     * @return HashMap&lt;数据表字段, 实体字段&gt;
     */
    public HashMap<String, String> getMapFieldProperty(String cdaCode) {
        if (mapFieldProperty.containsKey(cdaCode)) return mapFieldProperty.get(cdaCode);
        return null;
    }

    private void setErrorInfo(String errorInfo) {
        errorMessage.add(errorInfo);
        log.info(errorInfo);
    }

    /**
     * 设置字典映射：HashMap&lt;字典名称, HashMap&lt;数据表字段, 实体字段&gt;&gt;
     *
     * @param cdaCode     CDA代码
     * @param dbFieldName 数据表字段名称
     * @return cda代码与数据库字段对应的实体属性字段名称
     */
    public String getCdaCodeField(String cdaCode, String dbFieldName) {

        if (mapFieldProperty.containsKey(cdaCode)) {
            HashMap<String, String> resultMap = mapFieldProperty.get(cdaCode);
            if (resultMap.containsKey(dbFieldName)) return resultMap.get(dbFieldName);
        }
        return null;
    }

    /**
     * 读取字段与实体属性映射关系
     *
     * @param cdaCode CDA代码
     * @param xmlFile XML定义文件
     * @return 没有读到则返回空，否则返加HashMap&lt;数据表字段, 实体字段&gt;
     */
    public HashMap<String, String> readResultMap(String cdaCode, String xmlFile) {
        String errorInfo;
        errorMessage.clear();
        java.io.File file = new java.io.File(xmlFile);
        if (!file.exists()) {
            errorInfo = xmlFile + "文件不存在。";
            setErrorInfo(errorInfo);
            return null;
        }

        StringBuilder sb = GCLib.ReadFileResultMapStr(file);
        if (sb.length() == 0) {
            setErrorInfo(String.format("文件%s中没有找到<resultMap>节点。", xmlFile));
            return null;
        }

        log.info("开始读取resultMap文件" + xmlFile);
        Document doc;
        SAXReader reader = new SAXReader();
        try {
            doc = reader.read(new ByteArrayInputStream(sb.toString().getBytes()));
        } catch (DocumentException e) {
            errorInfo = String.format("SAXReader读文件%s错误：%s", xmlFile, e.getMessage());
            setErrorInfo(errorInfo);
            e.printStackTrace();
            return null;
        }

        log.info("读取resultMap节点。");
        int count = 0;
        String nodePath = "/resultMap/result";
        List<Node> listNode = doc.selectNodes(nodePath);
        if (listNode == null || listNode.size() == 0) {
            return null;
        }

        HashMap<String, String> map = new HashMap<>();
        for (Node node : listNode) {
            Element ele = (Element) node;
            String name = ele.attributeValue("property");
            String column = ele.attributeValue("column");
            if (!name.isEmpty() && !column.isEmpty()) {
                map.put(column, name);
            }
        }
        mapFieldProperty.put(cdaCode, map);
        return map;
    }

    /**
     * 设置对象的字段值
     *
     * @param fName  字段名称
     * @param fValue 字段值
     * @param entity 实体对象
     * @param <T>    泛型类
     * @return 没有读到则返回空，否则返加HashMap&lt;数据表字段, 实体字段&gt;
     */

    public <T> boolean setEntityValue(String fName, String fValue, T entity) {
        try {
            Field field = entity.getClass().getDeclaredField(fName);
            String type = field.getType().toString().toLowerCase();
            Object oval;
            if (type.endsWith(".string")) {
                oval = GCLib.CStr(fValue);
            } else if (type.contains("integer") || type.contains("long")) {
                oval = GCLib.CInt(fValue);
            } else if (type.contains("decimal")) {
                oval = GCLib.CDecimal(fValue);
            } else if (type.contains("double") || type.contains("float")) {
                oval = GCLib.CDouble(fValue);
            } else if (type.contains("boolean")) {
                oval = GCLib.CBool(fValue);
            } else if (type.contains("date")) {
                oval = GCLib.CDateTime(fValue);
            } else {
                oval = GCLib.CStr(fValue);
            }

            field.setAccessible(true);
            field.set(entity, oval);
        } catch (Exception ex) {
            String errorInfo = String.format("%s, %s错误:%s", fName, ex.getClass().toString(), ex.getMessage());
            setErrorInfo(errorInfo);
            return false;
        }
        return true;
    }

    /**
     * @param fName  字段名称
     * @param entity 实体对象
     * @param <T>    泛型类
     * @return 实体字段的值
     */
    public <T> String getEntityValue(String fName, T entity) {
        String val = "";
        try {
            Field field = entity.getClass().getDeclaredField(fName);
            field.setAccessible(true);
            val = GCLib.CStr(field.get(entity));
        } catch (Exception ex) {
            String errorInfo = String.format("%s, %s错误:%s", fName, ex.getClass().toString(), ex.getMessage());
            setErrorInfo(errorInfo);
        }
        return val;
    }
}
