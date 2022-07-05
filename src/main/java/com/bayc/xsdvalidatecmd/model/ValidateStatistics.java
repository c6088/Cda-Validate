package com.bayc.xsdvalidatecmd.model;

import java.time.LocalDate;

/*
 * 验证统计信息实体
 */
public class ValidateStatistics {
    /*
     * 构造函数
     */
    ValidateStatistics() {
        _validateDate = LocalDate.now();
        _okCount = 0;
        _grade = 0;
        _failCount = 0;
    }

    /*
     * 析构函数
     */
    public void finalize() {
        _validateDate = null;
    }

    /*
     * 验证日期
     */
    private LocalDate _validateDate;

    public LocalDate getValidateDate() {
        return _validateDate;
    }

    public void setValidateDate(LocalDate validateDate) {
        this._validateDate = validateDate;
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
     * 是否通过
     */
    private int _okCount;

    public int getOkCount() {
        return _okCount;
    }

    public void setIsOK(int okCount) {
        this._okCount = _okCount;
    }

    /**
     * 是否通过
     */
    private int _failCount;

    public int getFailCount() {
        return _failCount;
    }

    public void setFailCount(int failCount) {
        this._failCount = failCount;
    }
}
