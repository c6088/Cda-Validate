package com.bayc.xsdvalidatecmd.control;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

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
     * HashMap<字典名称, HashMap<数据表字段, 实体字段>>
     *
     * @return HashMap<字典名称, HashMap < 数据表字段, 实体字段>>
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
     * HashMap<数据表字段, 实体字段>>
     *
     * @return HashMap<数据表字段, 实体字段>>
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
     * 设置字典映射：HashMap<字典名称, HashMap<数据表字段, 实体字段>>
     *
     * @param cdaCode     CDA代码
     * @param dbFieldName 数据表字段名称
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
     * @return 没有读到则返回空，否则返加HashMap<数据表字段, 实体字段>
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

        log.info("开始读取resultMap文件" + xmlFile);
        Document doc;
        SAXReader reader = new SAXReader();
        try {
            doc = reader.read(new java.io.File(xmlFile));
        } catch (DocumentException e) {
            errorInfo = String.format("SAXReader读文件%s错误：%s", xmlFile, e.getMessage());
            setErrorInfo(errorInfo);
            e.printStackTrace();
            return null;
        }

        log.info("读取resultMap节点。");
        int count = 0;
        String nodePath = "mapper/resultMap/result";
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
     * @return 没有读到则返回空，否则返加HashMap<数据表字段, 实体字段>
     */
    public <T> boolean setEntityValue(String fName, Object fValue, T entity) {
        try {
            Field field = entity.getClass().getDeclaredField(fName);
            field.setAccessible(true);
            field.set(entity, fValue);
        } catch (Exception ex) {
            String errorInfo = String.format("%s, %s错误:%s", fName, ex.getClass().toString(), ex.getMessage());
            setErrorInfo(errorInfo);
            return false;
        }
        return true;
    }

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
