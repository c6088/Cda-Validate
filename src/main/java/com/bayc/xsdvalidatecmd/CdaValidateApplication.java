package com.bayc.xsdvalidatecmd;

import com.bayc.xsdvalidatecmd.control.CDAVerify;

import java.util.List;

public class CdaValidateApplication {
    /**
     * 入口主程序
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        boolean isOk;
        String cdaCode, primaryKey;
        cdaCode = "C0026";
        primaryKey = "10039844_1_1";

        CDAVerify cdaVerify = new CDAVerify();
        isOk = cdaVerify.Init();
        if (isOk) {
            isOk = cdaVerify.VerifyField(cdaCode, "SURGERY_CODE", "38.7x04");
            if (isOk) {
                isOk = cdaVerify.Verify(cdaCode, primaryKey);
            }
        }
        if (isOk) {
            System.out.println("Right");
        } else {
            System.out.println("Error");
            ShowErrorMessage(cdaVerify.getErrorMessage());
        }
    }

    /**
     * 显示错误信息列表
     *
     * @param errorList 错误信息列表
     */
    private static void ShowErrorMessage(List<String> errorList) {
        for (String errorInfo : errorList) {
            System.out.println(errorInfo);
        }
    }
}
