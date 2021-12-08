package com.bayc.xsdvalidatecmd;

import com.bayc.xsdvalidatecmd.handler.UserHandler;

import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author bayc
 * @packageName com.bayc.xsdvalidatecmd
 * @className XsdValidateTest
 * @description
 * @date 2021/3/29 上午10:44
 */
public class XsdValidateTest {
    public static void main(String[] args) {

        File file = new File("/Users/bayc/Downloads/hl7v3_xsd_2/multicacheschemas/PRPM_IN401030UV01.xsd");
        File xmlFile = new File("/Users/bayc/Downloads/医疗机构注册入参.xml");


        try {

            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = schemaFactory.newSchema(file);

            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            saxParserFactory.setSchema(schema);
            saxParserFactory.setNamespaceAware(true);
            saxParserFactory.setXIncludeAware(true);

            SAXParser saxParser = saxParserFactory.newSAXParser();
            UserHandler userHandler = new UserHandler();
            userHandler.setExceptionList(new ArrayList<>());
            saxParser.parse(xmlFile, userHandler);

            List<String> exceptionList = userHandler.getExceptionList();
            for (String error : exceptionList) {
                System.out.println(error);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
