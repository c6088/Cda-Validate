package com.bayc.xsdvalidatecmd;

import com.bayc.xsdvalidatecmd.control.DataConvertVerify;
import com.bayc.xsdvalidatecmd.control.DataExcel;
import com.bayc.xsdvalidatecmd.control.FieldPropertyMap;
import com.bayc.xsdvalidatecmd.dao.Cda26Dao;
import com.bayc.xsdvalidatecmd.entity.Cda26;
import com.bayc.xsdvalidatecmd.model.CdaDbHelper;
import com.bayc.xsdvalidatecmd.model.CdaNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CdaValidateApplication {
    final static Log log = LogFactory.getLog(XsdValidateCmd.class);
    final static List<String> errorMessage = new ArrayList<>();
    /**
     * 单个文档的节点数据
     */
    private static List<CdaNode> listCdaDefine = null;
    /**
     * 元素域数据字典
     */
    public static HashMap<String, String> mapDeDomain = null;
    /**
     * SqlSessionFactory
     */
    private static SqlSessionFactory ssf = null;
    /**
     * 数据验证对象
     */
    private static DataConvertVerify dataVerify = new DataConvertVerify();
    /**
     * 数据库字段与实体属性字段的映射对象
     */
    private static FieldPropertyMap fieldPropertyMap = new FieldPropertyMap();
    /**
     * dbHelper对象
     */
    private static CdaDbHelper dbHelper = new CdaDbHelper();

    /**
     * 入口程序
     */
    public static void main(String[] args) {
        String cdaCode = "C0026";
        String primaryKey = "10039844_1_1";
        boolean isOk = false;
        try {
            switch (cdaCode) {
                case "C0026":
                    isOk = validateC0026(primaryKey);
                    break;
                default:
                    break;
            }
        } catch (Exception ex) {
            setErrorInfo(String.format("执行Cda(%s, %s)数据认证时错误：%s", cdaCode, primaryKey, ex.getMessage()));
            ex.printStackTrace();
        }

        if (isOk) {
            log.info("OK");
        } else {
            log.info("FAIL");
            for (String errorInfo : errorMessage) {
                log.info(errorInfo);
            }
        }
    }

    /**
     * 初始化数据
     *
     * @return true成功 false失败
     */
    private boolean Init() {
        boolean isOk = false;
        String configFile = "mybatis-config.xml";
        try {
            Reader reader = Resources.getResourceAsReader(configFile);
            ssf = new SqlSessionFactoryBuilder().build(reader);
            reader.close();
        } catch (IOException ex) {
            String errorInfo = String.format("读取配置文件%s错误。", configFile);
            setErrorInfo(errorInfo);
            return false;
        }

        //读取数据库配置
        isOk = dbHelper.ReadMyBatisConfigFile(); //dbHelper.ReadConfigFile();
        if (!isOk) {
            setErrorInfo(dbHelper.getErrorMessage());
            return false;
        }
        isOk = dbHelper.CanConnectToDb();
        if (!isOk) {
            setErrorInfo(dbHelper.getErrorMessage());
            return false;
        }

        //读取Excel中的数据
        DataExcel excel = new DataExcel();
        isOk = excel.open("");
        if (!isOk) {
            setErrorInfo(excel.getErrorMessage());
            excel.close();
            return false;
        }
        mapDeDomain = excel.getDeDomain();
        excel.getMapDict();
        excel.getListCdaDefine();
        excel.close();
        setErrorInfo(excel.getErrorMessage());
        return errorMessage.size() == 0;
    }

    /**
     * 加入错误信息
     */
    private static void setErrorInfo(String errorInfo) {
        errorMessage.add(errorInfo);
        log.info(errorInfo);
    }

    /**
     * 加入错误信息列表
     */
    private static void setErrorInfo(List<String> listError) {
        for (String errorInfo : listError) {
            setErrorInfo(errorInfo);
        }
    }


    /**
     * 根据字段名称，获取CdaNode
     *
     * @param fieldName 字段名称
     * @return CdaNode, 没有找到时返回null
     */
    private static CdaNode getCdaNodeByFieldName(String fieldName) {
        int count = listCdaDefine.size();
        for (int i = 0; i < count; i++) {
            CdaNode node = listCdaDefine.get(i);
            if (node.getFieldName().compareTo(fieldName) == 0) {
                return node;
            }
        }
        return null;
    }

    /**
     * 验证手术知情同意书  src
     *
     * @param informedConsentNo 手术知情同意书单号
     * @return true正确 false有错误
     */
    public static boolean validateC0026(String informedConsentNo) throws IOException {
        log.info("读取相对路径下的mybatis.xml配置文件");
        SqlSession sqlSession = ssf.openSession(true);
        Cda26Dao dao = sqlSession.getMapper(Cda26Dao.class);
        Cda26 data = dao.queryById(informedConsentNo);
        errorMessage.clear();

        log.info("逐项验证数据的正确性。");
        int count = listCdaDefine.size();
        for (int i = 0; i < count; i++) {
            String fieldName, nameField;
            CdaNode node = listCdaDefine.get(i);
            if (node.getIsVerified() == 1) continue;  //校验过的不再校验
            if (node.getNodePath().isEmpty()) continue; //没有节点路径的点不是客户端传输的数据

            if (!dataVerify.isNodeOK(node)) {
                setErrorInfo(dataVerify.getErrorMessage());
            } else {
                node.setIsVerified(1);
                //如果有字典对应的名称字段
                fieldName = node.getNameCodeField();
                if (!fieldName.isEmpty()) {
                    //找名称字段对应的实体属性字段
                    nameField = fieldPropertyMap.GetCdaCodeField("C0026", "/src/main/resource/mapper/Cda26Dao.xml", fieldName);
                    if (nameField == null) {
                        setErrorInfo(String.format("字典代码（%s）对应的名称字段(%s)没有找到对应的映射。", node.getFieldName(), fieldName));
                    }
                    //找属性字段对应的CdaNode
                    CdaNode ele = getCdaNodeByFieldName(nameField);
                    if (ele == null) {
                        setErrorInfo(String.format("字典名称字段(%s)对应设置项不存在。", nameField, fieldName));
                    } else {
                        //对名称属性字段进行赋值
                        ele.setFieldValue(node.getNameCodeValue());
                        //标识不需要再校验
                        ele.setIsVerified(1);
                    }
                }
            }
        }

        boolean isOk = (errorMessage.size() == 0);
        if (isOk) {
            dao.update(data);
        }
        return isOk;
    }
}
