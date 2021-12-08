package com.bayc.xsdvalidatecmd;

import com.ctc.wstx.msv.W3CMultiSchemaFactory;
import com.ctc.wstx.stax.WstxInputFactory;
import org.codehaus.stax2.XMLStreamReader2;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidationSchemaFactory;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author bayc
 * @packageName com.bayc.xsdvalidatecmd
 * @className XmlValidateWoodStox
 * @description
 * @date 2021/3/29 下午3:06
 */
public class XmlValidateWoodStox {
    public static void main(String[] args) throws XMLStreamException, IOException, ParserConfigurationException, SAXException {
        File file = new File("config/sdschemas/SDA.xsd");
        File xmlFile = new File("F:\\北京天健\\应用\\集成平台\\互联互通\\CDA\\共享文档(标准版xml)\\1.病历概要.xml");

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        DOMSource domSource = new DOMSource(documentBuilder.parse(file));
        File xsd1 = new File("config/coreschemas/datatypes.xsd");
        File xsd2 = new File("config/coreschemas/datatypes-base.xsd");
        File xsd3 = new File("config/coreschemas/NarrativeBlock.xsd");
        File xsd4 = new File("config/coreschemas/voc.xsd");
        File xsd5 = new File("config/sdschemas/PCHIS.xsd");
        File xsd6 = new File("config/sdschemas/POCD_MT000040.xsd");
        DOMSource domSource1 = new DOMSource(documentBuilder.parse(xsd1));
        DOMSource domSource2 = new DOMSource(documentBuilder.parse(xsd2));
        DOMSource domSource3 = new DOMSource(documentBuilder.parse(xsd3));
        DOMSource domSource4 = new DOMSource(documentBuilder.parse(xsd4));
        DOMSource domSource5 = new DOMSource(documentBuilder.parse(xsd5));
        DOMSource domSource6 = new DOMSource(documentBuilder.parse(xsd6));
        Map<String, Source> sourceMap = new HashMap<>();
        sourceMap.put("xsd1", domSource1);
        sourceMap.put("xsd2", domSource2);
        sourceMap.put("xsd3", domSource3);
        sourceMap.put("xsd4", domSource4);
        sourceMap.put("xsd5", domSource5);
        sourceMap.put("xsd6", domSource6);
        sourceMap.put("s1", domSource);

        W3CMultiSchemaFactory w3CMultiSchemaFactory = new W3CMultiSchemaFactory();
        //XMLValidationSchema schema = w3CMultiSchemaFactory.createSchema(file.getParent(), sourceMap);

        // create the XSD schema from your schema file
        XMLValidationSchemaFactory schemaFactory = XMLValidationSchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        XMLValidationSchema validationSchema = schemaFactory.createSchema(file);

        // create the XML reader for your XML file
        WstxInputFactory inputFactory = new WstxInputFactory();
        XMLStreamReader2 xmlReader = inputFactory.createXMLStreamReader(xmlFile);


        try {
            // configure the reader to validate against the schema
            xmlReader.validateAgainst(validationSchema);

            // parse the XML
            while (xmlReader.hasNext()) {
                xmlReader.next();
            }

            // no exceptions, the XML is valid

        } catch (XMLStreamException e) {
            e.printStackTrace();
            // exceptions, the XML is not valid

        } finally {
            xmlReader.close();
        }
    }
}
