package com.bayc.xsdvalidatecmd;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * @author bayc
 * @packageName com.bayc.xsdvalidatecmd
 * @className FileCopyApplication
 * @description
 * @date 2021/5/17 下午6:13
 */
public class FileCopyApplication {
    public static void main(String[] args) throws IOException {
        File orignalFile = new File("/Users/bayc/Downloads/CDA_DOCUMENT");
        String destFile = "/Users/bayc/Downloads/ALl_CDA/";
        copyFile(orignalFile, destFile);
    }

    public static void copyFile(File originalFile, String destFile) throws IOException {
        if (originalFile.isDirectory()) {
            File[] files = originalFile.listFiles();
            for (File file : files) {
                copyFile(file, destFile);
            }
        } else {
            FileUtils.copyFile(originalFile, new File(destFile + originalFile.getParent().substring(originalFile.getParent().lastIndexOf("/") + 1) + "_" + originalFile.getName()));
        }
    }

}
