package com.bayc.xsdvalidatecmd.control;

import com.bayc.xsdvalidatecmd.enums.EnumDateType;
import com.bayc.xsdvalidatecmd.model.CdaNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * CDA节点数据转换与验证类
 */
public class DataConvertVerify {

    private Log log = LogFactory.getLog(DataConvertVerify.class);
    /**
     * 执行时的消息
     */
    private List<String> errorMessage = new ArrayList<>();

    /**
     * 执行过程中的错误信息列表，没有错误时返回null， 方法执行返回false时，查看此列表
     * 不是返回布尔型时，根据是否返回null，确定是否成功执行。
     */
    public List<String> getErrorMessage() {
        if (errorMessage.size() == 0) return null;
        return errorMessage;
    }

    private void setErrorInfo(String errorInfo) {
        errorMessage.add(errorInfo);
        log.info(errorInfo);
    }

    /**
     * 字典检测对象
     */
    private DictCheck dicCheck = new DictCheck();

    /**
     * 判断节点数据类型
     */
    public EnumDateType getNodeDateType(CdaNode node) {
        String str, val, strDataType;
        //是否是字典类型，枚举类型也是字典典型， 所以EnumDateType.S3也是EnumDateType.S2
        str = node.getCdaDictName();
        if (!str.isEmpty()) {
            return EnumDateType.S2;
        }
        //是否有数据元代码
        str = node.getMetaCode();
        if (!str.isEmpty()) {
            if (!DataExcel.mapDeDomain.containsKey(str)) {
                val = DataExcel.mapDeDomain.get(str);
                node.setCodomain(val);
            }
        }

        setNodeDomainInfo(node);
        strDataType = node.getDataType();
        EnumDateType dataType;
        switch (strDataType) {
            case "N":
                dataType = EnumDateType.N;
                break;
            case "D":
            case "DT":
                dataType = EnumDateType.DT;
                break;
            default: //"AN":
                dataType = EnumDateType.AN;
                break;

        }
        return dataType;
    }

    /**
     * 根据域值表达式中设置据类型字符串和数值表示 例如AN..10为AN和..10， N1..6,2为N和1..6,2，DT15为DT和15
     *
     * @param node CdaNode
     */
    public void setNodeDomainInfo(CdaNode node) {
        String str = node.getCodomain();
        if (str == null || str.isEmpty()) {
            return;
        }
        str = str.trim();
        int p = 0;
        int charLen = str.length();
        for (int i = 0; i < charLen; i++) {
            char c = str.charAt(i);
            if (c != '.' && (!Character.isDigit(c))) {
                p = i - 1;
                break;
            }
        }
        if (p >= 0) {
            node.setDataType(str.substring(0, p));
            if (p < charLen - 1) {
                node.setBaseNumber(str.substring(p + 1));
            }
        }
    }

    /**
     * 根据节点类型，验证节点是否正确
     *
     * @param node     CdaNode
     * @param dataType EnumDateType
     * @return true正确 false不正确
     */
    public boolean isNodeOK(CdaNode node, EnumDateType dataType) {
        boolean isOk;
        switch (dataType) {
            case N:
                isOk = isN(node);
                break;
            case D:
            case DT:
                isOk = isDT(node);
                break;
            case BLOB:
                isOk = isBLOB(node);
                break;
            case S2:
            case S3:
                isOk = isS2(node);
                break;

            default: //A, AN, S1
                isOk = isAN(node);
                break;
        }
        return isOk;
    }

    /**
     * 验证节点是否正确
     */
    public boolean isNodeOK(CdaNode node) {
        errorMessage.clear();
        EnumDateType dateType = getNodeDateType(node);
        return isNodeOK(node, dateType);
    }

    /**
     * 空值检查和值域未院时的检查，返回值1允许空并且是空， -1不允许空，是空值， 0不允许空，有值，需要进一步判断。
     *
     * @param node CdaNode
     * @return 1允许空并且是空， -1不允许空，是空值， 0不允许空，有值，需要进一步判断
     */
    private int emptyCheck(CdaNode node) {
        String text = node.getNodeValue().trim();
        boolean allowEmpty = !node.getConstraint().startsWith("R");
        if (text.isEmpty()) {
            if (!allowEmpty) {
                String errorInfo = String.format("节点%s(%s)不能为空, 日期格式应当为yyyyMMdd", node.getNodePath(), node.getFieldName());
                setErrorInfo(errorInfo);
                return -1;
            } else {
                node.setFieldValue("");
                return 1;
            }
        }

        String range; //值域
        String codomain = node.getCodomain();
        if (codomain.length() > 1) range = codomain.substring(1);
        else range = "";
        if (range.isEmpty()) {
            node.setFieldValue(text);
            boolean isOk = dictKeyCheck(node);
            if (isOk) return 1;
            return -1;
        }
        return 0;
    }

    /**
     * 是否是日期的D8或者DT14格式
     *
     * @param node CdaNode
     * @return true是, false不是
     */
    public boolean isDT(CdaNode node) {
        int rev = emptyCheck(node);
        if (rev == 1) return true;
        if (rev == -1) return false;
        String str = node.getBaseNumber();
        int v = GCLib.CInt(str);
        String formatStr;
        if (v == 8) formatStr = "yyyyMMdd";
        else formatStr = "yyyyMMddHHmmss";
        String text = node.getNodeValue().trim();
        Date date = GCLib.CDate(text, formatStr);
        if (date != null) {
            node.setFieldValue(text);
            return true;
        }
        String errorInfo = String.format("节点%s: %s, 不是%s格式的字符串。", node.getNodePath(), text, formatStr);
        setErrorInfo(errorInfo);
        return false;
    }

    /**
     * 是否是S2字符串
     *
     * @param node CdaNode
     * @return true是, false不是
     */
    public boolean isS2(CdaNode node) {
        int rev = emptyCheck(node);
        if (rev == 1) return true;
        if (rev == -1) return false;
        return dictKeyCheck(node);
    }

    /**
     * 是否是数值，允许前后有空格， trim后，只能是数字和一个小数点，最终数据还可以转换为Double不出错。
     *
     * @param node CdaNode
     * @return true是, false不是
     */
    public boolean isN(CdaNode node) {
        int rev = emptyCheck(node);
        if (rev == 1) return true;
        if (rev == -1) return false;
        String errorInfo;
        String text = node.getNodeValue();
        Boolean isOk = GCLib.IsNumberStr(text);
        if (!isOk) {
            errorInfo = String.format("节点%s: %s, 应当是数值。", node.getNodePath(), node.getNodeValue());
            setErrorInfo(errorInfo);
            return false;
        }

        try {
            double dbl = Double.parseDouble(text);
            String range = ""; //值域
            String codomain = node.getCodomain();
            if (codomain.length() > 1) range = codomain.substring(1);
            if (range.isEmpty()) {
                node.setFieldValue(text);
                isOk = dictKeyCheck(node);
                return isOk;
            }

            String[] arr;
            arr = range.split(".."); //格式：例N1..5, N..5, N5, N5,2
            int low = 0, hight = 0;
            if (arr.length == 2) {
                //格式：例N1..5, N..5
                low = (arr[0].isEmpty() ? 0 : Integer.parseInt(arr[0]));
                hight = Integer.parseInt(arr[1]);
                int charLen = text.length();
                if (charLen < low || charLen > hight) {
                    errorInfo = String.format("节点%s: %s, 值域应当是%s。", node.getNodePath(), text, codomain);
                    setErrorInfo(errorInfo);
                    return false;
                }
                node.setFieldValue(text);
            } else {
                //格式：例N5, N5,2
                DecimalFormat df;
                String val;
                int zscd, xscd;  //整数,整数长度, 小数,小数长度
                if (range.indexOf(",") >= 0) {
                    //格式：例N5,2
                    arr = range.split(",");
                    zscd = Integer.parseInt(arr[0]);
                    xscd = Integer.parseInt(arr[1]);
                    String fmt = GCLib.RepeatStr("#", xscd);
                    df = new DecimalFormat("#." + fmt);
                    val = df.format(dbl);
                    int p = val.indexOf('.'), len;
                    if (p > 0) {
                        len = val.substring(0, p).length();
                        if (len > zscd) {
                            errorInfo = String.format("节点%s: %s, 值域应当是%s。", node.getNodePath(), text, codomain);
                            setErrorInfo(errorInfo);
                            return false;
                        }
                    }
                    node.setFieldValue(val);
                } else {
                    //格式：例N5
                    df = new DecimalFormat("#");
                    val = df.format(dbl);
                    zscd = Integer.parseInt(range);
                    if (val.length() > zscd) {
                        errorInfo = String.format("节点%s: %s, 值域应当是%s。", node.getNodePath(), text, codomain);
                        setErrorInfo(errorInfo);
                        return false;
                    }
                    node.setFieldValue(val);
                }
            }
        } catch (NumberFormatException ex) {
            errorInfo = String.format("节点%s: %s, 应当是数值。", node.getNodePath(), text);
            setErrorInfo(errorInfo);
        } catch (Exception ex) {
            errorInfo = String.format("节点%s: %s, 应当是数值。Error: %s", node.getNodePath(), text, ex.getMessage());
            setErrorInfo(errorInfo);
        }
        isOk = dictKeyCheck(node);
        return isOk;
    }

    /**
     * 节点的值是否是字典中的键。
     *
     * @param node CdaNode
     * @return true是, false不是
     */
    private boolean dictKeyCheck(CdaNode node) {
        boolean isOk = true;
        String dataType = node.getDataType();
        boolean isDict = dataType.startsWith("S2") || dataType.startsWith("S3");
        if (isDict) {
            String dicname = node.getCdaDictName();
            if (!GCLib.IsNullAndEmpty(dicname)) {
                isOk = dicCheck.CheckKey(dicname, node.getNodeValue());
                if (!isOk) {
                    String errorInfo = String.format("节点%s: %s, 数据类型：%s, 不是标准%s中的值。Error: %s", node.getNodePath(), node.getNodeValue(), dataType, dicname);
                    setErrorInfo(errorInfo);
                    isOk = false;
                }
            }
        }
        return isOk;
    }

    /**
     * 节点值域是AN的判断
     *
     * @param node CdaNode
     * @return true是, false不是
     */
    public boolean isAN(CdaNode node) {
        int rev = emptyCheck(node);
        if (rev == 1) return true;
        if (rev == -1) return false;

        boolean isOk = true;
        String text = node.getNodeValue();
        int charLen = text.length();
        String range; //值域
        String codomain = node.getCodomain();
        if (codomain.length() > 1) range = codomain.substring(1);
        else range = "";
        if (range.isEmpty()) {
            node.setFieldValue(text);
            isOk = dictKeyCheck(node);
            return isOk;
        }

        String[] arr;
        String errorInfo;
        arr = range.split(".."); //格式：例AN1..5, AN..5
        int low = 0, hight = 0;
        if (arr.length == 2) {
            //格式：例N1..5, N..5
            low = (arr[0].isEmpty() ? 0 : Integer.parseInt(arr[0]));
            hight = Integer.parseInt(arr[1]);
            if (charLen < low || charLen > hight) {
                errorInfo = String.format("节点%s: %s, 值域应当是%s。", node.getNodePath(), text, codomain);
                setErrorInfo(errorInfo);
                return false;
            }
            node.setFieldValue(text);
        } else {
            //格式：例AN5
            if (charLen != GCLib.CInt(range)) {
                errorInfo = String.format("节点%s: %s, 值域应当是%s。", node.getNodePath(), text, codomain);
                setErrorInfo(errorInfo);
                return false;
            }
            node.setFieldValue(text);
        }
        isOk = dictKeyCheck(node);
        return isOk;
    }

    /**
     * 节点值域是BLOB的判断
     *
     * @param node CdaNode
     * @return true是, false不是
     */
    public boolean isBLOB(CdaNode node) {
        return true;
    }
}
