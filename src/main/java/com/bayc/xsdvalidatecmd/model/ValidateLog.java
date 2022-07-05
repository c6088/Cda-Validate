package com.bayc.xsdvalidatecmd.model;

import java.io.Serializable;
import java.util.Date;

/*
 * 验证日志实体
 */
public class ValidateLog implements Serializable {

    /*
     * 构造函数
     */
    ValidateLog() {
        _validateTime = new Date();
        _isOK = 0;
        _grade = 0;
        _validateInfo = "";
        _docID = "";
    }

    /*
     * 析构函数
     */
    public void finalize() {
        _validateTime = null;
        _validateInfo = null;
        _docID = null;
    }

    /*
     * 验证时间
     */
    private Date _validateTime;

    public Date getValidateTime() {
        return _validateTime;
    }

    public void setValidateTime(Date validateTime) {
        this._validateTime = validateTime;
    }

    /**
     * CDA代码
     */
    private String _cdaCode;

    public String getCdaCode() {
        return _cdaCode;
    }

    public void setCdaCode(String cdaCode) {
        this._cdaCode = cdaCode;
    }

    /**
     * 文档编号
     */
    private String _docID;

    public String getDocID() {
        return _docID;
    }

    public void setDocID(String docID) {
        this._docID = docID;
    }

    /**
     * 是否通过
     */
    private int _isOK;

    public int getIsOK() {
        return _isOK;
    }

    public void setIsOK(int isOK) {
        this._isOK = isOK;
    }

    /**
     * 验证级别: 0为天健验证，1为官方验证。
     */
    private int _grade;

    public int getGrade() {
        return _grade;
    }

    public void setGrade(int grade) {
        this._grade = grade;
    }

    /**
     * 验证返回消息
     */
    private String _validateInfo;

    public String getValidateInfo() {
        return _validateInfo;
    }

    public void setValidateInfo(String validateInfo) {
        this._validateInfo = _validateInfo;
    }
}
