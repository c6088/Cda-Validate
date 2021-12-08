package com.bayc.xsdvalidatecmd.handler;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.util.List;

/**
 * @author bayc
 * @packageName com.bayc.xsdvalidatecmd.handler
 * @className ValidatorHandler
 * @description
 * @date 2021/3/28 下午5:47
 */
public class ValidatorHandler implements ErrorHandler {
    private List<String> exceptionList;

    @Override
    public void warning(SAXParseException exception) throws SAXException {
        exceptionList.add("line:" + exception.getLineNumber() + "  col:" + exception.getColumnNumber() + "  " + exception.getMessage());
    }

    @Override
    public void error(SAXParseException exception) throws SAXException {
        exceptionList.add("line:" + exception.getLineNumber() + "  col:" + exception.getColumnNumber() + "  " + exception.getMessage());
    }

    @Override
    public void fatalError(SAXParseException exception) throws SAXException {
        exceptionList.add("line:" + exception.getLineNumber() + "  col:" + exception.getColumnNumber() + "  " + exception.getMessage());
    }

    public List<String> getExceptionList() {
        return exceptionList;
    }

    public void setExceptionList(List<String> exceptionList) {
        this.exceptionList = exceptionList;
    }
}
