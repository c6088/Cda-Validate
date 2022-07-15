package com.bayc.xsdvalidatecmd.control;

import com.bayc.xsdvalidatecmd.enums.EnumDateType;
import com.bayc.xsdvalidatecmd.model.CdaNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
     *
     * @return 错误信息列表
     */
    public List<String> getErrorMessage() {
        return errorMessage;
    }

    /**
     * 构造函数
     */
    public DataConvertVerify() {
    }

    /**
     * 析构函数
     */
    protected void finalize() {
        log = null;
        if (errorMessage != null) {
            errorMessage.clear();
            errorMessage = null;
        }
        if (dicCheck != null) {
            dicCheck = null;
        }
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
     *
     * @param node CdaNode节点
     * @return EnumDateType枚举值
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
        String str, val;
        str = node.getCodomain();
        if (str == null || str.isEmpty()) {
            return;
        }
        str = str.trim();
        int p = -1;
        int charLen = str.length();
        for (int i = 0; i < charLen; i++) {
            char c = str.charAt(i);
            if (c == '.' || Character.isDigit(c)) {
                break;
            } else {
                p = i;
            }
        }
        if (p >= 0) {
            val = str.substring(0, p + 1);
            node.setDataType(val);
        }
        if (p < charLen - 1) {
            val = str.substring(p + 1);
            node.setBaseNumber(val);
        }
        val = node.getBaseNumber();
        p = GCLib.parserInt(val);
        if (p > 0) {
            node.setConstraint("R"); //1..N是必填
        }
    }

    public void Init() {
        dicCheck.setDictData(DataExcel.mapDicts, DataExcel.mapDictVersion);
    }

    /**
     * 根据节点类型，验证节点是否正确
     *
     * @param node CdaNode
     * @return true正确 false不正确
     */
    public boolean isNodeOK(CdaNode node) {
        boolean isOk;
        EnumDateType dataType = this.getNodeDateType(node);
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
                String errorInfo = String.format("节点%s, 字段(%s)不能为空， 值域：%s。", node.getNodePath(), node.getFieldName(), node.getCodomain());
                setErrorInfo(errorInfo);
                return -1;
            } else {
                node.setFieldValue("");
                return 1;
            }
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
        boolean isOk = true;
        String dicName = node.getCdaDictName();
        if (!GCLib.IsNullAndEmpty(dicName)) {
            String errorInfo, key, val;
            key = node.getNodeValue();
            isOk = dicCheck.CheckKey(dicName, key);
            if (!isOk) {
                errorInfo = dicCheck.getInfoMessage();
                if (errorInfo.isEmpty()) {
                    errorInfo = String.format("不是字典中的值。节点%s: %s, 数据类型：%s, 字典：%s", node.getNodePath(), node.getNodeValue(), node.getDataType(), dicName);
                }
                setErrorInfo(errorInfo);
                isOk = false;
            }
            val = dicCheck.getKeyValue(dicName, key);
            if (val == null || val.isEmpty()) {
                errorInfo = String.format("字典(%s)中的键(%s)没有设置对应的值，请检查。 ", dicName, key);
                setErrorInfo(errorInfo);
            }
            node.setFieldValue(key);
            node.setNameCodeValue(val);
        }
        return isOk;
    }

    /**
     * 是否是数值，允许前后有空格， trim后，只能是数字和一个小数点，最终数据还可以转换为Double不出错。
     *
     * @param node CdaNode
     * @return true是, false不是
     */
    public boolean isN(CdaNode node) {
        //判断允许空值
        int rev = emptyCheck(node);
        if (rev == 1) return true;
        if (rev == -1) return false;
        Boolean isOk;
        String errorInfo;
        //判断是否是数字
        String text, val, range;
        text = GCLib.CStr(node.getNodeValue()).trim();
        int p;
        int charLen = text.length();
        range = node.getBaseNumber(); //值域
        int[] lowHigh = getRangeLowHigh(range);
        //小数点位置
        int xscd = lowHigh[2]; //小数长度
        if (xscd <= 0) {
            isOk = GCLib.IsNumberStr(text);
        } else {
            isOk = GCLib.IsDoubleStr(text);
            if (isOk) {
                //校验小数长度
                p = text.indexOf('.');
                if (p < 0 || p >= text.length() - 1) {
                    errorInfo = String.format("应当有小数。节点%s: %s, 应当是数值，值域：%s。", node.getNodePath(), node.getNodeValue(), node.getCodomain());
                    setErrorInfo(errorInfo);
                    return false;
                }
                val = range.substring(p + 1);
                if (val.length() != xscd) {
                    errorInfo = String.format("小数位数长度不对。节点%s: %s, 应当是数值，值域：%s。", node.getNodePath(), node.getNodeValue(), node.getCodomain());
                    setErrorInfo(errorInfo);
                    return false;
                }
            }
        }
        if (!isOk) {
            errorInfo = String.format("节点%s: %s, 应当是数值，值域：%s。", node.getNodePath(), node.getNodeValue(), node.getCodomain());
            setErrorInfo(errorInfo);
            return false;
        }
        if (charLen < lowHigh[0] || charLen > lowHigh[1]) {
            errorInfo = String.format("数值长度不正确。节点%s: %s, 值域应当是%s。", node.getNodePath(), text, node.getCodomain());
            setErrorInfo(errorInfo);
            return false;
        }
        node.setFieldValue(text);
        return true;
    }

    /**
     * 读取域值的范围上下限值
     *
     * @param range
     * @return int[3] 其中int[0]为最小数， int[2]为最大数, int[2] 为小数位数
     */
    private int[] getRangeLowHigh(String range) {
        String v1, v2, v3;
        int p, decimalPoint;
        int[] vals = new int[3];
        vals[0] = 0;
        vals[1] = 0;
        vals[2] = 0;
        if (range == null || range.isEmpty()) return vals;

        //取出小数点位数
        decimalPoint = range.indexOf(',');
        if (decimalPoint >= 0) {
            v3 = range.substring(decimalPoint);
            vals[2] = GCLib.CInt(v3);
            if (decimalPoint == 0) return vals;
            range = range.substring(0, decimalPoint);
        }

        //取出以..分隔的上下限值
        p = range.indexOf(".."); //格式：例1..5, ..5, ..*
        if (p >= 0) {
            //格式：例1..5, N..5
            if (p == 0) {
                v2 = range.substring(p + 2);
            } else {
                v1 = range.substring(0, p);
                vals[0] = GCLib.CInt(v1);
                if (p + 2 < range.length()) v2 = range.substring(p + 2);
                else v2 = "";
            }
            if (v2.compareTo("*") == 0) vals[1] = 2 ^ 21;
            else vals[1] = GCLib.CInt(v2);
        } else {
            vals[0] = GCLib.CInt(range);
            vals[1] = vals[0];
        }
        return vals;
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

        String text = GCLib.CStr(node.getNodeValue()).trim();
        int charLen = text.length();
        String range, codomain, errorInfo;
        codomain = node.getCodomain();
        range = node.getBaseNumber();
        if (range.isEmpty()) {
            node.setFieldValue(text);
            return true;
        }
        int[] lowHigh = getRangeLowHigh(range);
        if (charLen < lowHigh[0] || charLen > lowHigh[1]) {
            errorInfo = String.format("数据长度不正确。节点%s: %s, 值域应当是%s。", node.getNodePath(), text, codomain);
            setErrorInfo(errorInfo);
            return false;
        }
        node.setFieldValue(text);
        return true;
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
