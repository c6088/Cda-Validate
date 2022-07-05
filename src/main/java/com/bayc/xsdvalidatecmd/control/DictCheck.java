package com.bayc.xsdvalidatecmd.control;

import java.util.HashMap;
import java.util.Map;

/**
 * 字典检查, 检查前需要初始化
 */
public class DictCheck {
    /**
     * 字典名称对应的映射字典
     */
    private HashMap<String, HashMap<String, String>> dictData;

    public HashMap<String, HashMap<String, String>> getDictData() {
        return dictData;
    }

    public void set_dictData(HashMap<String, HashMap<String, String>> dictData) {
        this.dictData = dictData;
    }

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
     * 字典是否包含键
     */
    public boolean CheckKey(String dictName, String key) {
        HashMap<String, String> map = dictData.get(dictName);
        return map.containsKey(key);
    }

    /**
     * 字典是否包含值
     */
    public boolean CheckValue(String dictName, String value) {
        HashMap<String, String> map = dictData.get(dictName);
        return map.containsValue(value);
    }

    /**
     * 通过值查字典的键
     */
    public String ValueToKey(String dictName, String value) {
        HashMap<String, String> map = dictData.get(dictName);
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
