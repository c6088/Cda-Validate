package com.bayc.xsdvalidatecmd.handler;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Stack;

/**
 * @author bayc
 * @packageName com.bayc.xsdvalidatecmd.handler
 * @className UserHandler
 * @description
 * @date 2021/3/29 上午11:48
 */
public class UserHandler extends DefaultHandler {
    private List<String> exceptionList;

    private Stack<String> elementUrl = new Stack<>();
    private List<SAXParseException> errorMessage = new ArrayList<>();
    private String element = "";
    private String uri = "";
    private String qname = "";

    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws IOException, SAXException {
        return super.resolveEntity(publicId, systemId);
    }

    @Override
    public void notationDecl(String name, String publicId, String systemId) throws SAXException {
        super.notationDecl(name, publicId, systemId);
    }

    @Override
    public void unparsedEntityDecl(String name, String publicId, String systemId, String notationName) throws SAXException {
        super.unparsedEntityDecl(name, publicId, systemId, notationName);
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        super.setDocumentLocator(locator);
    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        super.startPrefixMapping(prefix, uri);
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        super.endPrefixMapping(prefix);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        if (localName != null && !localName.isEmpty())
            element = localName;
        else
            element = qName;
        this.uri = uri;
        this.qname = qName;
        elementUrl.push(element);

        if (errorMessage.size() > 0) {
            for (SAXParseException msg : errorMessage) {
                exceptionList.add("line:" + msg.getLineNumber() + " col:" + msg.getColumnNumber() + " " + getElementUri() + " " + msg.getMessage());
                System.out.println("line:" + msg.getLineNumber() + " col:" + msg.getColumnNumber() + " " + getElementUri() + " " + msg.getMessage());
            }
            errorMessage.clear();
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);

        elementUrl.pop();
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        super.ignorableWhitespace(ch, start, length);
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
        super.processingInstruction(target, data);
    }

    @Override
    public void skippedEntity(String name) throws SAXException {
        super.skippedEntity(name);
    }

    @Override
    public void warning(SAXParseException exception) throws SAXException {
        errorMessage.add(exception);
    }

    @Override
    public void error(SAXParseException exception) throws SAXException {
        errorMessage.add(exception);
    }

    @Override
    public void fatalError(SAXParseException exception) throws SAXException {
        errorMessage.add(exception);
    }

    public String getElement() {
        return element;
    }

    public String getElementUri() {
        String tempUrl = "";
        ListIterator<String> stringListIterator = elementUrl.listIterator();
        while (stringListIterator.hasNext()) {
            tempUrl += "/" + stringListIterator.next();
        }
        return tempUrl;
    }

    public List<String> getExceptionList() {
        return exceptionList;
    }

    public void setExceptionList(List<String> exceptionList) {
        this.exceptionList = exceptionList;
    }
}
