package com.bayc.xsdvalidatecmd.model;

import java.io.Serializable;

/**
 * 数据元目录(HealthElementDirectory)实体类
 *
 * @author makejava
 * @since 2021-09-18 17:46:35
 */
public class HealthElementDirectory implements Serializable {
    private static final long serialVersionUID = -65929192474562679L;

    private Integer id;
    /**
     * 数据元标识符
     */
    private String deId;
    /**
     * 数据元编码
     */
    private String deCode;
    /**
     * 数据元名称
     */
    private String deName;
    /**
     * 数据元分类编码
     */
    private String deClassCode;
    /**
     * 数据元分类
     */
    private String deClass;
    /**
     * 数据元描述
     */
    private String deDescription;
    /**
     * 数据元数据类型
     */
    private String deDatatype;
    /**
     * 数据元数据类型描述
     */
    private String deDatatypeDescription;
    /**
     * 数据元允许值类型
     */
    private String deDataValuetype;
    /**
     * 数据元允许值/值域名称
     */
    private String deDataValues;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDeId() {
        return deId;
    }

    public void setDeId(String deId) {
        this.deId = deId;
    }

    public String getDeCode() {
        return deCode;
    }

    public void setDeCode(String deCode) {
        this.deCode = deCode;
    }

    public String getDeName() {
        return deName;
    }

    public void setDeName(String deName) {
        this.deName = deName;
    }

    public String getDeClassCode() {
        return deClassCode;
    }

    public void setDeClassCode(String deClassCode) {
        this.deClassCode = deClassCode;
    }

    public String getDeClass() {
        return deClass;
    }

    public void setDeClass(String deClass) {
        this.deClass = deClass;
    }

    public String getDeDescription() {
        return deDescription;
    }

    public void setDeDescription(String deDescription) {
        this.deDescription = deDescription;
    }

    public String getDeDatatype() {
        return deDatatype;
    }

    public void setDeDatatype(String deDatatype) {
        this.deDatatype = deDatatype;
    }

    public String getDeDatatypeDescription() {
        return deDatatypeDescription;
    }

    public void setDeDatatypeDescription(String deDatatypeDescription) {
        this.deDatatypeDescription = deDatatypeDescription;
    }

    public String getDeDataValuetype() {
        return deDataValuetype;
    }

    public void setDeDataValuetype(String deDataValuetype) {
        this.deDataValuetype = deDataValuetype;
    }

    public String getDeDataValues() {
        return deDataValues;
    }

    public void setDeDataValues(String deDataValues) {
        this.deDataValues = deDataValues;
    }

}
