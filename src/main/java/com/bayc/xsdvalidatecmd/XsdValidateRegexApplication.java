package com.bayc.xsdvalidatecmd;

import com.bayc.xsdvalidatecmd.enums.DEValueType;
import com.bayc.xsdvalidatecmd.handler.UserHandler;
import com.bayc.xsdvalidatecmd.model.HealthElementDirectory;
import org.apache.commons.io.FileUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author BaYcT
 * @version 1.0
 * @description:
 * @date 2021-12-08 10:42
 */
public class XsdValidateRegexApplication {

    private static HashMap<String, HealthElementDirectory> DE_CODE_DICT = new HashMap<>();
    private static HashMap<String, HashMap<String, String>> CDA_DICT = new HashMap<>();

    private static SimpleDateFormat format_str_dt15 = new SimpleDateFormat("yyyyMMddHHmmss");
    private static SimpleDateFormat format_str_dt8 = new SimpleDateFormat("yyyyMMdd");
    private static SimpleDateFormat format_date_dt14 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat format_date_dt8 = new SimpleDateFormat("yyyy-MM-dd");
    private static Pattern patternNumber;
    private static List<String> errorMessages = new ArrayList<>();
    static Validator validator = null;
    static SAXParser parser = null;
    static UserHandler userHandler = new UserHandler();


    public static void main(String[] args) throws DocumentException, IOException, ParserConfigurationException, SAXException {
        if (args == null) {
            printLog("请输入文件或者文件夹，可输入多个");
            return;
        }

        String cdadatasetPath = "config/数据集-2016版.xlsx";
        loadExcel(cdadatasetPath);

        String cdaDictPath = "config/CDA字典信息.xlsx";
        loadCDADICTExcel(cdaDictPath);

        patternNumber = Pattern.compile("[0-9]*");


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
        userHandler.setExceptionList(errorMessages);


        for (String filePath : args) {
            printLog("准备验证：" + filePath);

            if (new File(filePath).isDirectory()) {
                List<String> fileList = getAllPath(filePath, ".xml");
                for (String xmlPath : fileList) {
                    checkXml(xmlPath);
                    writeLog(xmlPath);
                }
            } else {
                checkXml(filePath);
                writeLog(filePath);
            }
        }
    }

    private static void checkXml(String xmlPath) throws IOException {
        errorMessages.clear();
        validate(xmlPath);
        if (errorMessages.size() > 0) {
            writeLog(xmlPath);
            return;
        }
        try {
            SAXReader reader = new SAXReader();
            //2.加载xml
            Document document = reader.read(new File(xmlPath));
            Map map = new HashMap();
            // 获得命名空间
            String nsURI = document.getRootElement().getNamespaceURI();
            map.put("xmlns", nsURI);
            // 创建解析路径，就是在普通的解析路径前加上map里的key值
            XPath xPath = document.createXPath("//xmlns:observation[@classCode='OBS'][@moodCode='EVN']");
            xPath.setNamespaceURIs(map);
            // 这样就拿到结果了
            List<Node> nodes = xPath.selectNodes(document);

            for (Node node : nodes) {
                nodeCheck(node);
            }
        } catch (Exception ex) {
            errorMessages.add(ex.getMessage());
        }

    }


    private static void nodeCheck(Node node) {
        Document document = node.getDocument();
        Map map = new HashMap();
        String nsURI = document.getRootElement().getNamespaceURI();
        map.put("xmlns", nsURI);
        XPath xPath = document.createXPath("./xmlns:code[@code][@codeSystem='2.16.156.10011.2.2.1'][@codeSystemName='卫生信息数据元目录']|./xmlns:value");
        xPath.setNamespaceURIs(map);
        List<Node> nodes = xPath.selectNodes(node);
        if (nodes.size() != 2) {
            return;
        }
        /**
         * 数据元编码
         */
        String de_code = "";

        //在字典中为code
        String value = "";
        String codeSystem = "";
        String codeSystemName = "";
        String displayName = "";
        DEValueType deValueType = DEValueType.elementValue;
        for (Node tempNode : nodes) {
            if ("code".equals(tempNode.getName())) {
                de_code = tempNode.selectSingleNode("@code").getText();
            } else if ("value".equals(tempNode.getName())) {
                // 取value节点中value属性值，如 <value xsi:type="TS" value="20130102123422"></value>
                Node valueNode = tempNode.selectSingleNode("@value");
                if (null != valueNode) {
                    value = valueNode.getText();
                    deValueType = DEValueType.attributeValue;
                    continue;
                }

                //取value节点中字典值，如 <value xsi:type="CD" code="1" codeSystem="2.16.156.10011.2.3.1.271" codeSystemName="患者类型代码表"></value>
                List<Node> dicts = tempNode.selectNodes("@code|@displayName|@codeSystem|@codeSystemName");
                if (dicts.size() > 2) {
                    for (Node dictNode : dicts) {
                        if ("code".equals(dictNode.getName())) {
                            value = dictNode.getText();
                        } else if ("displayName".equals(dictNode.getName())) {
                            displayName = dictNode.getText();
                        } else if ("codeSystem".equals(dictNode.getName())) {
                            codeSystem = dictNode.getText();
                        } else if ("codeSystemName".equals(dictNode.getName())) {
                            codeSystemName = dictNode.getText();
                        }
                    }
                    deValueType = DEValueType.dictValue;
                    continue;
                }
                //如以上都没有属性，则最终取节点值，如  <value xsi:type="ST">门（急）诊号</value>
                value = tempNode.getText();
            }
        }

        if (!DE_CODE_DICT.containsKey(de_code)) {
            //printLog("不存在数据元【" + de_code + "】的对应规则");
            return;
        }

        HealthElementDirectory healthElementDirectory = DE_CODE_DICT.get(de_code);
        String deName = healthElementDirectory.getDeName();
//        if (
////                "引用型".equals(healthElementDirectory.getDeDataValuetype()) ||
//                "枚举型".equals(healthElementDirectory.getDeDataValuetype())
//        ) {
//            //printLog("数据元【" + de_code + "】【" + deName + "】暂不进行字典验证!");
//            return;
//        }

        switch (healthElementDirectory.getDeDatatype()) {
            case "D":
                if (value != null && value.length() == 8) {
                    try {
                        Date date = format_str_dt8.parse(value);
                    } catch (ParseException e) {
                        printLog("数据元【" + de_code + "】【" + deName + "】日期格式错误!【" + value + "】");
                        return;
                    }
                } else {
                    printLog("数据元【" + de_code + "】【" + deName + "】日期格式【" + healthElementDirectory.getDeDatatypeDescription() + "】不是标准的日期!【" + value + "】");
                }
                break;
            case "DT":
                if (value != null && value.length() == 15) {
                    try {
                        if ('T' == value.charAt(8)) {
                            value = value.replace("T", "");
                            Date date = format_str_dt15.parse(value);
                        } else {
                            printLog("数据元【" + de_code + "】【" + deName + "】【" + healthElementDirectory.getDeDatatypeDescription() + "】格式错误!【" + value + "】");
                        }
                    } catch (ParseException e) {
                        printLog("数据元【" + de_code + "】【" + deName + "】【" + healthElementDirectory.getDeDatatypeDescription() + "】格式错误!【" + value + "】");
                        return;
                    }
                } else {
                    try {
                        Date date = format_str_dt15.parse(value);
                    } catch (ParseException e) {
                        printLog("数据元【" + de_code + "】【" + deName + "】格式【" + healthElementDirectory.getDeDatatypeDescription() + "】不是标准的日期!【" + value + "】");
                        return;
                    }
                }
                break;
            case "L":
                if (!"true".equals(value.toLowerCase()) &&
                        !"false".equals(value.toLowerCase())) {
                    printLog("数据元【" + de_code + "】【" + deName + "】格式【" + healthElementDirectory.getDeDatatypeDescription() + "】错误!【" + value + "】 应为【true/false】");
                    return;
                }
                break;
            case "N":
                try {
                    BigDecimal bd = new BigDecimal(value);
                } catch (NumberFormatException numberFormat) {
                    printLog("数据元【" + de_code + "】【" + deName + "】格式【" + healthElementDirectory.getDeDatatypeDescription() + "】错误!【" + value + "】不是数字！");
                    return;
                }
                String deDatatypeDescription = healthElementDirectory.getDeDatatypeDescription().replace("N", "");
                int minLength = 0;
                int maxLength = 0;
                int decimalLength = 0;

                Map<String, Integer> typeLength = getTypeLength(deDatatypeDescription);
                minLength = typeLength.get("minLength");
                maxLength = typeLength.get("maxLength");
                decimalLength = typeLength.get("decimalLength");

                // 先判断长度
                if (value.length() >= minLength && value.length() <= maxLength) {
                    //长度验证通过
                } else {
                    printLog("数据元【" + de_code + "】【" + deName + "】格式【" + healthElementDirectory.getDeDatatypeDescription() + "】错误!【" + value + "】长度应在" + minLength + "与" + maxLength + "之间");
                    return;
                }

                //开始判断小数位与整数位
                minLength -= decimalLength;
                maxLength -= decimalLength;
                if (minLength <= 0) {
                    minLength = 0;
                }

                if (value.indexOf(".") > 0) {
                    minLength -= 1;
                    maxLength -= 1;

                    if (value.indexOf(".") >= minLength && value.indexOf(".") <= maxLength) {
                        //整数部分长度正确,继续判断小数位
                        if ((value.length() - value.indexOf(".") - 1) != decimalLength) {
                            printLog("数据元【" + de_code + "】【" + deName + "】格式【" + healthElementDirectory.getDeDatatypeDescription() + "】错误!【" + value + "】精度应为" + decimalLength);
                            return;
                        }
                    } else {
                        printLog("数据元【" + de_code + "】【" + deName + "】格式【" + healthElementDirectory.getDeDatatypeDescription() + "】错误!【" + value + "】整数位长度应在" + minLength + "与" + maxLength + "之间");
                        return;
                    }
                } else {
                    if (value.length() >= minLength && value.length() <= maxLength) {

                    } else {
                        printLog("数据元【" + de_code + "】【" + deName + "】格式【" + healthElementDirectory.getDeDatatypeDescription() + "】错误!【" + value + "】整数位长度应在" + minLength + "与" + maxLength + "之间");
                        return;
                    }
                }
                break;
            case "S1":
                String strdeDatatypeDescription = healthElementDirectory.getDeDatatypeDescription().replace("A", "").replace("N", "");
                int strMinLength = 0;
                int strMaxLength = 0;

                Map<String, Integer> strLength = getTypeLength(strdeDatatypeDescription);
                strMinLength = strLength.get("minLength");
                strMaxLength = strLength.get("maxLength");
                // 先判断长度
                if (value.length() >= strMinLength && value.length() <= strMaxLength) {
                    //长度验证通过
                } else {
                    printLog("数据元【" + de_code + "】【" + deName + "】格式【" + healthElementDirectory.getDeDatatypeDescription() + "】错误!【" + value + "】长度应在" + strMinLength + "与" + strMaxLength + "之间");
                    return;
                }
                // 数字类型的字符串
                if (healthElementDirectory.getDeDatatypeDescription().startsWith("N")) {
                    Matcher matcher = patternNumber.matcher(value);
                    if (matcher.matches()) {
                        //数字验证通过
                    } else {
                        printLog("数据元【" + de_code + "】【" + deName + "】格式【" + healthElementDirectory.getDeDatatypeDescription() + "】错误!【" + value + "】应全部为数字！");
                        return;
                    }
                }
                break;
            case "S2":
            case "S3":
                //引用型数据
                String dictcode = healthElementDirectory.getDeDataValues();
                if (CDA_DICT.containsKey(dictcode)) {

                } else if (CDA_DICT.containsKey(de_code)) {
                    dictcode = de_code;
                } else {
                    printLog("数据元【" + de_code + "】【" + deName + "】无对应字典信息【" + de_code + "/" + dictcode + "】");
                    return;
                }

                if (!CDA_DICT.get(dictcode).containsKey(value)) {
                    printLog("数据元【" + de_code + "】【" + deName + "】字典【" + dictcode + "】中无此编码信息【" + value + "】");
                    return;
                }

                if (!CDA_DICT.get(dictcode).get(value).equals(displayName)) {
                    printLog("数据元【" + de_code + "】【" + deName + "】字典【" + dictcode + "】中编码【" + value + "】对应值应为【" + CDA_DICT.get(dictcode).get(value) + "】 实际值为【" + displayName + "】");
                    return;
                }
                break;
        }

    }

    private static void loadExcel(String excelPath) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(excelPath);
        XSSFWorkbook sheets = new XSSFWorkbook(fileInputStream);
        XSSFSheet sheet = sheets.getSheet("数据表详细字段信息");
        int lastRowNum = sheet.getLastRowNum();
        XSSFRow row;
        XSSFCell cell;
        String de_code = "";
        String value_type = "";
        for (int i = 1; i < lastRowNum; i++) {
            row = sheet.getRow(i);
            HealthElementDirectory healthElementDirectory = new HealthElementDirectory();
            healthElementDirectory.setDeCode(row.getCell(1).getStringCellValue());
            healthElementDirectory.setDeName(row.getCell(2).getStringCellValue());
            healthElementDirectory.setDeDescription(row.getCell(3).getStringCellValue());
            healthElementDirectory.setDeDatatype(row.getCell(4).getStringCellValue());
            healthElementDirectory.setDeDatatypeDescription(row.getCell(5).getStringCellValue());
            healthElementDirectory.setDeDataValuetype(row.getCell(6).getStringCellValue());
            healthElementDirectory.setDeDataValues(row.getCell(7).getStringCellValue());

            if (!DE_CODE_DICT.containsKey(healthElementDirectory.getDeCode())) {
                DE_CODE_DICT.put(healthElementDirectory.getDeCode(), healthElementDirectory);
            }
        }
        sheets.close();
    }

    private static void loadCDADICTExcel(String cdaDictPath) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(cdaDictPath);
        XSSFWorkbook sheets = new XSSFWorkbook(fileInputStream);
        XSSFSheet sheet = sheets.getSheet("CDA字典");
        int lastRowNum = sheet.getLastRowNum();
        XSSFRow row;
        XSSFCell cell;
        String dict_name = "";
        String dict_code = "";
        String code = "";
        String value = "";

        for (int i = 1; i < lastRowNum; i++) {
            row = sheet.getRow(i);
            try {
                dict_code = row.getCell(0).getStringCellValue();
                dict_name = row.getCell(1).getStringCellValue();
                code = row.getCell(2).getStringCellValue();
                value = row.getCell(3).getStringCellValue();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            if (!CDA_DICT.containsKey(dict_code)) {
                CDA_DICT.put(dict_code, new HashMap<>());
            }

            if (!CDA_DICT.get(dict_code).containsKey(code)) {
                CDA_DICT.get(dict_code).put(code, value);
            }
        }
        sheets.close();
    }

    private static Map<String, Integer> getTypeLength(String deDatatypeDescription) {

        HashMap<String, Integer> ret = new HashMap<>();

        int minLength = 0;
        int maxLength = 0;
        int decimalLength = 0;
        if (deDatatypeDescription.contains(".")) {
            while (deDatatypeDescription.contains("..")) {
                deDatatypeDescription = deDatatypeDescription.replace("..", ".");
            }
            String[] split = deDatatypeDescription.split("\\.");
            if (!"".equals(split[0])) {
                minLength = Integer.parseInt(split[0]);
            }
            if (split[1].contains(",")) {
                String[] split1 = split[1].split(",");
                maxLength = Integer.parseInt(split1[0]);
                decimalLength = Integer.parseInt(split1[1]);
            } else {
                maxLength = Integer.parseInt(split[1]);
            }
        } else {
            if (deDatatypeDescription.contains(",")) {
                String[] split = deDatatypeDescription.split(",");
                maxLength = Integer.parseInt(split[0]);
                minLength = maxLength;
                decimalLength = Integer.parseInt(split[1]);
            } else {
                maxLength = Integer.parseInt(deDatatypeDescription);
            }
        }

        ret.put("minLength", minLength);
        ret.put("maxLength", maxLength);
        ret.put("decimalLength", decimalLength);
        return ret;
    }


    public static List<String> getAllPath(String directoryPath, String suffix) {
        List<String> ret = new ArrayList<>();
        File[] files = new File(directoryPath).listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                ret.addAll(getAllPath(file.getPath(), suffix));
            } else {
                if (file.getName().endsWith(suffix)) {
                    ret.add(file.getPath());
                }
            }
        }
        return ret;
    }


    public static void printLog(String message) {
        System.out.println(message);
        errorMessages.add(message);
    }

    public static void writeLog(String xmlPath) throws IOException {
        String logPath = "logs";
        File file = new File(logPath);
        File xmlFile = new File(xmlPath);
        String xmlName = xmlFile.getName().substring(0, xmlFile.getName().lastIndexOf("."));
        if (errorMessages.size() == 0) {
            FileUtils.writeLines(new File(logPath + File.separator + xmlName + "_success.txt"), errorMessages, false);
        } else {
            FileUtils.writeLines(new File(logPath + File.separator + xmlName + "_error.txt"), errorMessages, false);
        }
    }

    public static void validate(String file) throws IOException {
        validate(new File(file));
    }

    public static void validate(File file) throws IOException {
        try {
            errorMessages.clear();
            parser.parse(file, userHandler);
        } catch (SAXException e) {
            printLog(e.getMessage());
        }
    }
}
