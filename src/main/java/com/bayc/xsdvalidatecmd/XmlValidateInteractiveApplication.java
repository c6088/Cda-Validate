package com.bayc.xsdvalidatecmd;

import com.bayc.xsdvalidatecmd.model.ValidateXmlResult;
import com.bayc.xsdvalidatecmd.model.ValidateXmlResultFlag;
import org.dom4j.Element;
import org.dom4j.util.XMLErrorHandler;
import org.xml.sax.SAXException;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.*;
import java.util.List;

/**
 * @author BaYcT
 * @version 1.0
 * @description:
 * @date 2021-12-06 16:56
 */
public class XmlValidateInteractiveApplication {


    public static void main(String[] args) throws FileNotFoundException {
        String xsdPath = "";
        String xml = "";

        InputStream xsdInputStream = new FileInputStream(xsdPath);
        ValidateXmlResult validateXmlResult = validateXml(xsdInputStream, xml, "127.0.0.1");
        System.out.println(validateXmlResult.getErrorInfo());
    }


    public static ValidateXmlResult validateXml(InputStream xsdStream, String xml, String clientIp) {
        //验证结果对象
        ValidateXmlResult validateXmlResult = new ValidateXmlResult();

        //创建SchemaFactory实例，该类不是线程安全的
        SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        //从输入流读取xsd文件内容
        Source xsdSource = new StreamSource(xsdStream);
        //根据xsd内容创建schema
        Schema schema = null;
        try {
            schema = schemaFactory.newSchema(xsdSource);
        } catch (SAXException e) {
            e.printStackTrace();
        }
        //根据schema创建验证器Validator
        Validator validator = schema.newValidator();
        InputStream xmlInputStream = getInputStreamOfXml(xml, clientIp);
        //根据xml创建Source
        Source xmlSource = new StreamSource(xmlInputStream);
        //创建一个DOM4J的XMLErrorHandler类，该类实现了jdk的org.xml.sax.ErrorHander接口
        XMLErrorHandler errorHandler = new XMLErrorHandler();
        //向验证器添加错误处理后，当验证发生错误时就不会抛出异常了，而是把异常信息存入errorHandler中
        validator.setErrorHandler(errorHandler);
        //从Source中读取xml内容进行验证，如果验证失败不会抛出异常
        // 如果验证失败会将错误原因（Element的text属性存储）及位置(用Element的line与column属性存储位置)存储到errorHandler
        try {
            validator.validate(xmlSource);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //XMLErrorHandler类有个Element类型的属性error，如果验证失败就会向error添加子元素（Element）
        //因此，我们可以通过判断error属性是否有子元素从而知道是否验证失败
        if (errorHandler.getErrors().hasContent()) {
            //验证失败
            StringBuffer result = new StringBuffer();
            //获取所有失败的element
            List<Element> elements = errorHandler.getErrors().elements();
            int i = 1;
            for (Element element : elements) {
                //获取错误元素的位置
                String errorLine = element.attribute("line").getValue();
                String errorColumn = element.attribute("column").getValue();
                //获取验证失败的原因
                String errorReson = element.getText();

                result.append("第" + i + "个错误\nerror line:" + errorLine + "\nerror column:" + errorColumn + "\nerror reason:" + errorReson + "\n");
                i++;
                //System.out.println("error line："+errorLine);
                //列号，发生异常的文本结尾的列号。--解释来自SAXException类的解释
                //System.out.println("error column："+errorColumn);
                //System.out.println("error reason:"+errorReson);
            }
            validateXmlResult.setErrorInfo(result);
            validateXmlResult.setValidateXmlResultFlag(ValidateXmlResultFlag.FAIL);
        } else {
            validateXmlResult.setValidateXmlResultFlag(ValidateXmlResultFlag.SUCCESS);
            //System.out.println("验证成功");
        }
        return validateXmlResult;
    }

    public static InputStream getInputStreamOfXml(String xml, String clientIp) {
        ByteArrayInputStream inputStreamOfXml = null;
        try {
            inputStreamOfXml = new ByteArrayInputStream(xml.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return inputStreamOfXml;
    }
}
