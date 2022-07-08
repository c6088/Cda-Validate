package com.bayc.xsdvalidatecmd.control;

import java.util.HashMap;
import java.util.Map;

/**
 * 字典检查, 检查前需要初始化
 */
public class DictCheck {
    /**
     * 字典启用的当前版本号， 字典名称=>字典含版本号全称
     */
    private HashMap<String, String> dictVersion = new HashMap<>();

    public HashMap<String, String> getDictVersion() {
        return dictVersion;
    }

    public void setDictVersion(HashMap<String, String> dictVersion) {
        if (dictVersion != null) {
            this.dictVersion = dictVersion;
        }
    }

    /**
     * 字典名称对应的映射字典
     */
    private HashMap<String, HashMap<String, String>> dictData = null;

    public HashMap<String, HashMap<String, String>> getDictData() {
        return dictData;
    }

    public void setDictData(HashMap<String, HashMap<String, String>> dictData, HashMap<String, String> dictVersion) {
        this.dictData = dictData;
        this.dictVersion = dictVersion;
    }

    public String getInfoMessage() {
        return infoMessage;
    }

    public void setInfoMessage(String infoMessage) {
        this.infoMessage = infoMessage;
    }

    private String infoMessage = "";

    /**
     * 构造函数
     */
    public DictCheck() {

    }

    /*
     * 析构函数
     */
    public void finalize() {
        dictData = null;
        dictVersion = null;
    }

    /**
     * 根据字典名称取对应字典， 参数dictName不能为空。
     */
    public HashMap<String, String> getDict(String dictName) {
        if (dictData.containsKey(dictName)) {
            return dictData.get(dictName);
        }
        return null;
    }

    /**
     * 设值字典名称的字典， 参数dictName和map都不能为空
     */
    public void setDict(String dictName, HashMap<String, String> map) {
        if (map == null) return;
        dictData.put(dictName, map);
    }

    /**
     * 读取设置字典的当前版本的名称
     *
     * @param dicName 字典名称
     * @return 设置的使用的版本名称
     */
    public String getCurrentVersion(String dicName) {
        if (dictVersion.containsKey(dicName)) {
            return dictVersion.get(dicName);
        }
        return dicName;
    }

    /**
     * 字典是否包含键
     */
    public boolean CheckKey(String dicName, String key) {
        infoMessage = "";
        String dictName = getCurrentVersion(dicName);
        HashMap<String, String> map = dictData.get(dictName);
        if (map == null) {
            infoMessage = String.format("不存在名称为%s的字典", dictName);
            return false;
        }
        return map.containsKey(key);
    }

    /**
     * 读取字典键对应的值
     */
    public String getKeyValue(String dictName, String key) {
        infoMessage = "";
        dictName = getCurrentVersion(dictName);
        HashMap<String, String> map = dictData.get(dictName);
        if (map == null) {
            infoMessage = String.format("不存在名称为%s的字典", dictName);
            return "";
        }
        return map.get(key);
    }

    /**
     * 字典是否包含值
     */
    public boolean CheckValue(String dictName, String value) {
        infoMessage = "";
        dictName = getCurrentVersion(dictName);
        HashMap<String, String> map = dictData.get(dictName);
        if (map == null) {
            infoMessage = String.format("不存在名称为%s的字典", dictName);
            return false;
        }
        return map.containsValue(value);
    }

    /**
     * 通过值查字典的键
     */
    public String ValueToKey(String dictName, String value) {
        infoMessage = "";
        dictName = getCurrentVersion(dictName);
        HashMap<String, String> map = dictData.get(dictName);
        if (map == null) {
            infoMessage = String.format("不存在名称为%s的字典", dictName);
            return "";
        }
        String key = "";
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (value == entry.getValue()) {
                key = entry.getKey();
                break;
            }
        }
        return key;
    }
}
