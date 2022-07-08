package com.bayc.xsdvalidatecmd.model;

import com.bayc.xsdvalidatecmd.enums.EnumCodeName;

import java.io.Serializable;

/*
 * CDA节点实体
 */
public class CdaNode implements Serializable {

    /*
     * 构造函数
     */
    public CdaNode() {
        _nodeValue = "";
        _nodePath = "";
        _fieldValue = "";
        _cdaDictName = "";
        _constraint = "";
        _baseNumber = "";
    }

    /*
     * 析构函数
     */
    public void finalize() {
        _nodePath = null;
        _nodeValue = null;
        _fieldValue = null;
    }

    /**
     * 节点路径 xpath
     */
    private String _nodePath;

    public String getNodePath() {
        if (_nodePath == null) _nodePath = "";
        return _nodePath;
    }

    public void setNodePath(String nodePath) {
        this._nodePath = nodePath;
    }

    /**
     * 节点值 xml文档节点的Text
     */
    private String _nodeValue;

    public String getNodeValue() {
        if (_nodeValue == null) _nodeValue = "";
        return _nodeValue;
    }

    public void setNodeValue(String nodeValue) {
        this._nodeValue = nodeValue;
    }

    /**
     * 数据库表的名称
     */
    private String _tableName;

    public String getTableName() {
        if (_tableName == null) _tableName = "";
        return _tableName;
    }

    public void setTableName(String tableName) {
        this._tableName = tableName;
    }

    /**
     * 数据库表对应的字段名称
     */
    private String _fieldName;

    public String getFieldName() {
        if (_fieldName == null) _fieldName = "";
        return _fieldName;
    }

    public void setFieldName(String fieldName) {
        this._fieldName = fieldName;
    }

    /**
     * 数据库表字段的描述
     */
    private String _fieldDescription;

    public String getFieldDescription() {
        if (_fieldDescription == null) _fieldDescription = "";
        return _fieldDescription;
    }

    public void setFieldDescription(String fieldDescription) {
        this._fieldDescription = fieldDescription;
    }

    /**
     * CDA代码
     */
    private String _cdaCode;

    public String getCdaCode() {
        if (_cdaCode == null) _cdaCode = "";
        return _cdaCode;
    }

    public void setCdaCode(String cdaCode) {
        this._cdaCode = cdaCode;
    }

    /**
     * 节点基数 例1..5 / 0..* / 0..1 / 1..5,2 / 8
     */
    private String _baseNumber;

    public String getBaseNumber() {
        if (_baseNumber == null) _baseNumber = "";
        return _baseNumber;
    }

    public void setBaseNumber(String baseNumber) {
        this._baseNumber = baseNumber;
    }

    /**
     * 节点约束 R必填，R2有必填，O可选
     */
    private String _constraint;

    public String getConstraint() {
        if (_constraint == null) _constraint = "";
        return _constraint;
    }

    public void setConstraint(String constraint) {
        this._constraint = constraint;
    }

    /**
     * 数据类型 S1, S2, S3, L, DT, D, T, N, BLOB
     */
    private String _dataType;

    public String getDataType() {
        if (_dataType == null) _dataType = "";
        return _dataType;
    }

    public void setDataType(String dataType) {
        this._dataType = dataType;
    }

    /**
     * 数据元代码
     */
    private String _metaCode;

    public String getMetaCode() {
        if (_metaCode == null) _metaCode = "";
        return _metaCode;
    }

    public void setMetaCode(String metaCode) {
        this._metaCode = metaCode;
    }

    /**
     * 字段值的值域 A, N, AN, D8, DT14, DT15, T6 例: N2，N5,2，AN..32, N1..2, N..4
     */
    private String _codomain;

    public String getCodomain() {
        if (_codomain == null) _codomain = "";
        return _codomain;
    }

    public void setCodomain(String codomain) {
        this._codomain = codomain;
    }

    /**
     * CDA字典名称
     */
    private String _cdaDictName;

    public String getCdaDictName() {
        if (_cdaDictName == null) _cdaDictName = "";
        return _cdaDictName;
    }

    public void setCdaDictName(String cdaDictName) {
        this._cdaDictName = cdaDictName;
    }

    /**
     * 验证代码还是名称
     */
    private EnumCodeName _eCodeName = EnumCodeName.Name;

    public EnumCodeName getEnumCodeName() {
        return _eCodeName;
    }

    public void setEnumCodeName(EnumCodeName eCodeName) {
        this._eCodeName = eCodeName;
    }

    /**
     * 字典代码对应的名称字段
     */
    private String _nameCodeField = "";

    public String getNameCodeField() {
        return _nameCodeField;
    }

    public void setNameCodeField(String nameCodeField) {
        this._nameCodeField = nameCodeField;
    }

    /**
     * 字典代码对应名称字段的值
     */
    private String _nameCodeValue;

    public String getNameCodeValue() {
        if (_nameCodeValue == null) _nameCodeValue = "";
        return _nameCodeValue;
    }

    public void setNameCodeValue(String nameCodeValue) {
        this._nameCodeValue = nameCodeValue;
    }

    /**
     * 数据库表对应的字段的值
     */
    private String _fieldValue;

    public String getFieldValue() {
        return _fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this._fieldValue = fieldValue;
    }

    /**
     * 是否校验过 1是, 0没有
     */
    private int _isVerified = 0;

    public int getIsVerified() {
        return _isVerified;
    }

    public void setIsVerified(int isVerified) {
        this._isVerified = isVerified;
    }
}
