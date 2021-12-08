package com.bayc.xsdvalidatecmd;

import com.bayc.xsdvalidatecmd.handler.UserHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author bayc
 * @packageName com.bayc.xsdvalidate
 * @className XsdTest
 * @description
 * @date 2021/3/24 上午10:23
 */
public class XsdValidateCmd {
    final static Log log = LogFactory.getLog(XsdValidateCmd.class);
    final static List<String> errorMessage = new ArrayList<>();
    static Validator validator = null;
    static SAXParser parser = null;
    static UserHandler userHandler = new UserHandler();

    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
        if (args == null) {
            System.out.println("请输入文件或者文件夹，可输入多个");
            return;
        }
        log.info("开始读取文件");

        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        File file = new File("config/sdschemas/SDA.xsd");
        Schema schema = schemaFactory.newSchema(file);
        //获取基于 SAX 的解析器的实例
        SAXParserFactory factory = SAXParserFactory.newInstance();
        //解析器在解析时验证 XML 内容。
        //factory.setValidating(true);
        //指定由此代码生成的解析器将提供对 XML 名称空间的支持。
        factory.setNamespaceAware(true);
        factory.setXIncludeAware(true);

        factory.setSchema(schema);
        //使用当前配置的工厂参数创建 SAXParser 的一个新实例。
        parser = factory.newSAXParser();
//        //设置 XMLReader 的基础实现中的特定属性。核心功能和属性列表可以在 [url]http://sax.sourceforge.net/?selected=get-set[/url] 中找到。
//        parser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage",
//                "http://www.w3.org/2001/XMLSchema");
//        parser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaSource", file.getPath());
        userHandler.setExceptionList(errorMessage);
//        documentBuilderFactory.setSchema(schema);
//
//        validator = schema.newValidator();
//        ValidatorHandler validatorHandler = new ValidatorHandler();
//        validatorHandler.setExceptionList(errorMessage);
//        validator.setErrorHandler(validatorHandler);

        for (String filePath : args) {
            log.info("准备验证：" + filePath);
            validate(filePath);
        }
    }

    public static void validate(String file) throws IOException {
        validate(new File(file));
    }

    public static void validate(File file) throws IOException {
        if (file.isFile()) {
            if (!file.getName().toLowerCase().endsWith(".xml")) {
                log.error(file.getPath() + ":只能验证后缀为.xml的文件");
                return;
            }
            String parentPath = file.getParent();
            String fileName = file.getName().substring(0, file.getName().lastIndexOf("."));
            String filePath = file.getPath();
            String validateFile = parentPath + File.separator + fileName + "_validate_error.txt";

            try {
                errorMessage.clear();

                parser.parse(file, userHandler);

                //validator.validate(new StreamSource(file));
                if (errorMessage.size() == 0) {
                    log.info(filePath + ":验证成功！");
                } else {
                    writeErrorMessage(filePath, validateFile, errorMessage);
                }
            } catch (SAXException e) {
                e.printStackTrace();
            }
        } else if (file.isDirectory()) {
            log.info("开始验证目录：" + file.getPath());
            File[] files = file.listFiles((tempfile, tempfilename) -> tempfilename.toLowerCase().endsWith(".xml"));
            for (File tempfile : files) {
                validate(tempfile);
            }
        } else {
            log.error("无效的文件或文件夹：" + file.getPath());
        }
    }

    static void writeErrorMessage(String xmlPath, String errorFilePath, List<String> messages) throws IOException {
        File errorFile = new File(errorFilePath);
        for (String msg : messages) {
            log.error(xmlPath + ":" + msg);
        }
        FileUtils.writeLines(errorFile, "utf8", messages, false);
    }
}
