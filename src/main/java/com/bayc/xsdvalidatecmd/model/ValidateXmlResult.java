package com.bayc.xsdvalidatecmd.model;

/**
 * @author xiaopeng zhou
 * @date 2020/8/5
 * <p>
 * 存储验证结果信息与验证成功与否的标志
 */
public class ValidateXmlResult {
    //用于判断验证是否成功
    private ValidateXmlResultFlag validateXmlResultFlag;
    //判断失败时存储失败信息
    private StringBuffer errorInfo;

    public ValidateXmlResult() {
        errorInfo = new StringBuffer();
        validateXmlResultFlag = ValidateXmlResultFlag.FAIL;
    }

    public StringBuffer getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(StringBuffer errorInfo) {
        this.errorInfo = errorInfo;
    }

    public ValidateXmlResultFlag getValidateXmlResultFlag() {
        return validateXmlResultFlag;
    }

    public void setValidateXmlResultFlag(ValidateXmlResultFlag validateXmlResultFlag) {
        this.validateXmlResultFlag = validateXmlResultFlag;
    }
}
