package com.bayc.xsdvalidatecmd.control;

import com.bayc.xsdvalidatecmd.enums.EnumDateType;
import com.bayc.xsdvalidatecmd.model.CdaNode;

import java.text.DecimalFormat;
import java.util.Date;

/*
 * CDA节点数据转换与验证类
 */
public class DataConvertVerify {

    /**
     * 执行时的消息
     */
    private String _infoMessage = "";

    public String getInfoMessage() {
        return _infoMessage;
    }

    private DictCheck dicCheck = new DictCheck();

    public boolean isNodeOK(CdaNode node) {
        _infoMessage = "";
        EnumDateType dateType = getNodeDateType(node);
        return isNodeOK(node, dateType);
    }

    public EnumDateType getNodeDateType(CdaNode node) {
        String str;
        str = node.getCdaDictName();
        if (!str.isEmpty()) {
            return EnumDateType.S2;
        }

        str = node.getMetaCode();
        if (!str.isEmpty()) {
            
        }
        return EnumDateType.A;
    }

    /**
     * 执行时的消息
     *
     * @param node     CdaNode
     * @param dataType EnumDateType
     * @return true正确 false不正确
     */
    public boolean isNodeOK(CdaNode node, EnumDateType dataType) {
        boolean isOk = false;
        switch (dataType) {
            case A:
                isOk = IsA(node);
                break;
            case AN:
                isOk = IsAN(node);
                break;
            case N:
                isOk = IsN(node);
                break;
            case D:
                isOk = IsD8(node);
                break;
            case DT:
                isOk = IsDT14(node);
                break;
            case BLOB:
                isOk = IsBLOB(node);
                break;
            case S2:
                isOk = IsS2(node);
                break;
            case S3:
                isOk = IsS3(node);
                break;

            default: //S1
                isOk = IsS1(node);
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
    private int EmptyCheck(CdaNode node) {
        String text = node.getNodeValue().trim();
        boolean allowEmpty = !node.getConstraint().startsWith("R");
        if (text.isEmpty()) {
            if (!allowEmpty) {
                _infoMessage = String.format("节点%s(%s)不能为空, 日期格式应当为yyyyMMdd", node.getNodePath(), node.getFieldName());
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
            boolean isOk = DictKeyCheck(node);
            if (isOk) return 1;
            return -1;
        }
        return 0;
    }

    /**
     * 是否是日期的D8格式yyyyMMdd。
     *
     * @param node CdaNode
     * @return true是, false不是
     */
    public boolean IsD8(CdaNode node) {
        int rev = EmptyCheck(node);
        if (rev == 1) return true;
        if (rev == -1) return false;
        return IsDT(node, "yyyyMMdd");
    }

    /**
     * 是否是日期格式的字符串
     *
     * @param node      CdaNode
     * @param formatStr 日期格式的字符串
     * @return true是, false不是
     */
    public boolean IsDT(CdaNode node, String formatStr) {
        String text = node.getNodeValue().trim();
        Date date = GCLib.CDate(text, formatStr);
        if (date != null) {
            node.setFieldValue(text);
            return true;
        }
        _infoMessage = String.format("节点%s: %s, 不是%s格式的字符串。", node.getNodePath(), text, formatStr);
        return false;
    }

    /**
     * 是否是日期的DT14格式yyyyMMddHHmmss的字符串
     *
     * @param node CdaNode
     * @return true是, false不是
     */
    public boolean IsDT14(CdaNode node) {
        int rev = EmptyCheck(node);
        if (rev == 1) return true;
        if (rev == -1) return false;
        return IsDT(node, "yyyyMMddHHmmss");
    }

    /**
     * 是否是S1字符串
     *
     * @param node CdaNode
     * @return true是, false不是
     */
    public boolean IsS1(CdaNode node) {
        return true;
    }

    /**
     * 是否是S2字符串
     *
     * @param node CdaNode
     * @return true是, false不是
     */
    public boolean IsS2(CdaNode node) {
        return true;
    }

    /**
     * 是否是S3字符串
     *
     * @param node CdaNode
     * @return true是, false不是
     */
    public boolean IsS3(CdaNode node) {
        return true;
    }

    /**
     * 是否是数字字符串
     *
     * @param node CdaNode
     * @return true是, false不是
     */
    public boolean IsA(CdaNode node) {
        int rev = EmptyCheck(node);
        if (rev == 1) return true;
        if (rev == -1) return false;
        String text = node.getNodeValue().trim();
        if (GCLib.IsNumberStr(text)) {
            node.setFieldValue((text));
            return true;
        }
        _infoMessage = String.format("节点%s: %s, 不是由数字0-9组成的字符串。", node.getNodePath(), text);
        return false;
    }

    /**
     * 是否是数值，允许前后有空格， trim后，只能是数字和一个小数点，最终数据还可以转换为Double不出错。
     *
     * @param node CdaNode
     * @return true是, false不是
     */
    public boolean IsN(CdaNode node) {
        int rev = EmptyCheck(node);
        if (rev == 1) return true;
        if (rev == -1) return false;

        String text = node.getNodeValue();
        Boolean isOk = GCLib.IsNumberStr(text);
        if (!isOk) {
            _infoMessage = String.format("节点%s: %s, 应当是数值。", node.getNodePath(), node.getNodeValue());
            return false;
        }

        try {
            double dbl = Double.parseDouble(text);
            String range = ""; //值域
            String codomain = node.getCodomain();
            if (codomain.length() > 1) range = codomain.substring(1);
            if (range.isEmpty()) {
                node.setFieldValue(text);
                isOk = DictKeyCheck(node);
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
                    _infoMessage = String.format("节点%s: %s, 值域应当是%s。", node.getNodePath(), text, codomain);
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
                            _infoMessage = String.format("节点%s: %s, 值域应当是%s。", node.getNodePath(), text, codomain);
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
                        _infoMessage = String.format("节点%s: %s, 值域应当是%s。", node.getNodePath(), text, codomain);
                        return false;
                    }
                    node.setFieldValue(val);
                }
            }
        } catch (NumberFormatException ex) {
            _infoMessage = String.format("节点%s: %s, 应当是数值。", node.getNodePath(), text);
        } catch (Exception ex) {
            _infoMessage = String.format("节点%s: %s, 应当是数值。Error: %s", node.getNodePath(), text, ex.getMessage());
        }
        isOk = DictKeyCheck(node);
        return isOk;
    }

    /**
     * 节点的值是否是字典中的键。
     *
     * @param node CdaNode
     * @return true是, false不是
     */
    private boolean DictKeyCheck(CdaNode node) {
        boolean isOk = true;
        String dataType = node.getDataType();
        boolean isDict = dataType.startsWith("S2") || dataType.startsWith("S3");
        if (isDict) {
            String dicname = node.getCdaDictName();
            if (!GCLib.IsNullAndEmpty(dicname)) {
                isOk = dicCheck.CheckKey(dicname, node.getNodeValue());
                if (!isOk) {
                    _infoMessage = String.format("节点%s: %s, 数据类型：%s, 不是标准%s中的值。Error: %s", node.getNodePath(), node.getNodeValue(), dataType, dicname);
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
    public boolean IsAN(CdaNode node) {
        int rev = EmptyCheck(node);
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
            isOk = DictKeyCheck(node);
            return isOk;
        }

        String[] arr;
        arr = range.split(".."); //格式：例AN1..5, AN..5
        int low = 0, hight = 0;
        if (arr.length == 2) {
            //格式：例N1..5, N..5
            low = (arr[0].isEmpty() ? 0 : Integer.parseInt(arr[0]));
            hight = Integer.parseInt(arr[1]);
            if (charLen < low || charLen > hight) {
                _infoMessage = String.format("节点%s: %s, 值域应当是%s。", node.getNodePath(), text, codomain);
                return false;
            }
            node.setFieldValue(text);
        } else {
            //格式：例AN5
            if (charLen != GCLib.CInt(range)) {
                _infoMessage = String.format("节点%s: %s, 值域应当是%s。", node.getNodePath(), text, codomain);
                return false;
            }
            node.setFieldValue(text);
        }
        isOk = DictKeyCheck(node);
        return isOk;
    }

    /**
     * 节点值域是BLOB的判断
     *
     * @param node CdaNode
     * @return true是, false不是
     */
    public boolean IsBLOB(CdaNode node) {
        return true;
    }
}
