package com.bayc.xsdvalidatecmd.control;

import com.bayc.xsdvalidatecmd.dao.Cda26Dao;
import com.bayc.xsdvalidatecmd.entity.Cda26;
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

/**
 * CDA验证类
 */
public class CDAVerify {
    /**
     * 日志对象
     */
    private Log log = LogFactory.getLog(CDAVerify.class);
    /**
     * 错误信息列表
     */
    private List<String> errorMessage = new ArrayList<>();

    /**
     * 读取错误信息列表
     *
     * @return 错误信息列表
     */
    public List<String> getErrorMessage() {
        return errorMessage;
    }

    /**
     * SqlSessionFactory
     */
    private SqlSessionFactory ssf = null;
    /**
     * 数据验证对象
     */
    private DataConvertVerify dataVerify = new DataConvertVerify();
    /**
     * 数据库字段与实体属性字段的映射对象，mybatis的resources/mapper对应的Dao.xml中设置
     */
    private FieldPropertyMap fieldPropertyMap = new FieldPropertyMap();
    /**
     * CdaDbHelper对象

     private CdaDbHelper dbHelper = new CdaDbHelper();
     */

    /**
     * DataExcel对象
     */
    private DataExcel excel = new DataExcel();
    /**
     * 单个文档的节点数据， 在Excel中设置
     */
    private List<CdaNode> listCdaDefine = null;
    /**
     * 元素值域数据字典， 在Excel中设置
     */
    private HashMap<String, String> mapDeDomain = null;
    /**
     * 单个文档字段名对属性名称字典， 在Excel中设置
     */
    private HashMap<String, String> mapFieldProperty = null;

    /**
     * 构造函数
     */
    public CDAVerify() {
    }

    /**
     * 析构函数
     */
    public void finalize() {
        log = null;
        if (errorMessage != null) {
            errorMessage.clear();
            errorMessage = null;
        }
        if (listCdaDefine != null) {
            listCdaDefine = null;
        }
        if (ssf != null) {
            ssf = null;
        }
        if (dataVerify != null) {
            dataVerify = null;
        }
        if (fieldPropertyMap != null) {
            fieldPropertyMap = null;
        }
//        if (dbHelper != null) {
//            dbHelper = null;
//        }
        if (excel != null) {
            excel = null;
        }
    }

    /**
     * 验证CDA文档数据是否正确
     *
     * @param cdaCode    CDA代码
     * @param primaryKey 表主键(多主键用,分隔)
     * @return true 正确，false不正确，请查看errorMessage
     */
    public boolean Verify(String cdaCode, String primaryKey) {
        if (cdaCode.isEmpty()) cdaCode = "C0026";
        if (primaryKey.isEmpty()) primaryKey = "10039844_1_1";
        //读取CDA节点定义
        listCdaDefine = excel.getListCdaDefine(cdaCode);
        if (excel.getErrorMessage().size() > 0) {
            setErrorInfo(excel.getErrorMessage());
            return false;
        }

        //按文档类别进行验证
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
        }
        return isOk;
    }

    /**
     * 初始化数据：包括读取数据库配置，读取Excel中的设置信息，修改了Excel中的数据，需要重新初始化数据。
     *
     * @return true成功 false失败
     */
    public boolean Init() {
        boolean isOk = false;
        this.cdaCode = "";
        errorMessage.clear();
        try {
            Reader reader = Resources.getResourceAsReader(CdaConfig.mybatisConfigFile);
            ssf = new SqlSessionFactoryBuilder().build(reader);
            reader.close();
        } catch (IOException ex) {
            String errorInfo = String.format("读取配置文件%s错误。", CdaConfig.mybatisConfigFile);
            setErrorInfo(errorInfo);
            return false;
        }

        //读取Excel中的数据
        isOk = excel.dataLoadFromJsonFile();
        if (!isOk) {
            isOk = excel.open();
            if (!isOk) {
                setErrorInfo(excel.getErrorMessage());
                excel.close();
                return false;
            }
            excel.getMapDict();
            excel.getMapDictVersion();
            excel.getListCdaDefine();
            mapDeDomain = excel.getDeDomain();
            excel.close();
            excel.dataWriteToJsonFile();
            setErrorInfo(excel.getErrorMessage());
        }
        dataVerify.Init();
        return errorMessage.size() == 0;
    }

    /**
     * 加入错误信息到errorMessage
     *
     * @param errorInfo 错误信息
     */
    private void setErrorInfo(String errorInfo) {
        errorMessage.add(errorInfo);
        log.info(errorInfo);
    }

    /**
     * 加入错误信息列表到errorMessage
     *
     * @param listError 错误信息列表到
     */
    private void setErrorInfo(List<String> listError) {
        if (listError == null) return;
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
    private CdaNode getCdaNodeByFieldName(String fieldName) {
        if (fieldName == null) return null;
        int count = listCdaDefine.size();
        for (int i = 0; i < count; i++) {
            CdaNode node = listCdaDefine.get(i);
            if (fieldName.compareTo(node.getFieldName()) == 0) {
                return node;
            }
        }
        return null;
    }

    /**
     * 初始化表与字段的映射关系
     *
     * @param cdaCode
     * @return true成功，false失败
     */
    public boolean getCdaMap(String cdaCode) {
        mapFieldProperty = this.fieldPropertyMap.getMapFieldProperty(cdaCode);
        if (mapFieldProperty == null) {
            String xmlFile = getCdaMapperFile(cdaCode);
            this.fieldPropertyMap.readResultMap("C0026", xmlFile);
            if (excel.getErrorMessage().size() > 0) {
                setErrorInfo(excel.getErrorMessage());
                return false;
            }
            mapFieldProperty = this.fieldPropertyMap.getMapFieldProperty(cdaCode);
        }
        return true;
    }

    /**
     * CDA代码数据库对应的Mapper文件， TBC：等待完成
     *
     * @param cdaCode
     * @return Mapper文件
     */
    public String getCdaMapperFile(String cdaCode) {
        String fileName;
        switch (cdaCode) {
            case "C0026":
                fileName = "src/main/resources/mapper/Cda26Dao.xml";
                break;
            default:
                fileName = "";
                break;
        }
        return fileName;
    }

    /**
     * 验证手术知情同意书  src
     *
     * @param informedConsentNo 手术知情同意书单号
     * @return true正确 false有错误
     */
    public boolean validateC0026(String informedConsentNo) throws IOException {
        boolean isOk;
        String cdaCode = "C0026";
        //读取字段与实体属性的映射
        isOk = getCdaMap(cdaCode);
        if (!isOk) return false;

        //读取数据
        SqlSession sqlSession = ssf.openSession(true);
        Cda26Dao dao = sqlSession.getMapper(Cda26Dao.class);
        Cda26 data = dao.queryById(informedConsentNo);
        errorMessage.clear();

        //从data中读取数据, 写入listCdaDefine的nodeValue属性中。
        int count = listCdaDefine.size();
        for (int i = 0; i < count; i++) {
            String fieldName, entityFieldName, val;
            CdaNode node = listCdaDefine.get(i);
            fieldName = node.getFieldName();
            if (node.getNodePath().isEmpty()) continue;

            entityFieldName = mapFieldProperty.get(fieldName);
            if (entityFieldName == null || entityFieldName.isEmpty()) {
                setErrorInfo(String.format("表：%s,字段：%s,节点(%s), 没有对应的映射属性。", node.getTableName(), node.getFieldName(), node.getNodePath()));
                continue;
            }
            val = this.fieldPropertyMap.<Cda26>getEntityValue(entityFieldName, data);
            node.setNodeValue(val);
        }
        if (errorMessage.size() > 0) {
            return false;
        }

        //校验nodeValue的值是否合理
        for (int i = 0; i < count; i++) {
            String fieldName, entityPropertyName, codeField, nameField;
            CdaNode node = listCdaDefine.get(i);
            if (node.getIsVerified() == 1) continue;    //校验过的不再校验
            if (node.getNodePath().isEmpty()) continue; //没有节点路径的点不是客户端传输的数据

            fieldName = node.getFieldName();
//            if (node.getFieldName().compareTo("DIAGNOSIS_CODE") == 0)
//                i = i;

            if (dataVerify.isNodeOK(node)) {

                node.setIsVerified(1);
                entityPropertyName = fieldPropertyMap.getCdaCodeField(cdaCode, fieldName);
                this.fieldPropertyMap.<Cda26>setEntityValue(entityPropertyName, node.getFieldValue(), data);

                //如果有字典对应的名称字段
                fieldName = node.getNameCodeField();
                if (!fieldName.isEmpty()) {
                    //找名称字段对应的实体属性字段
                    entityPropertyName = fieldPropertyMap.getCdaCodeField(cdaCode, fieldName);
                    if (entityPropertyName == null) {
                        setErrorInfo(String.format("字典字段（%s）代码对应的名称字段(%s)没有找到对应的映射。", node.getFieldName(), fieldName));
                    } else {
                        //找属性字段对应的CdaNode
                        CdaNode ele = getCdaNodeByFieldName(fieldName);
                        if (ele == null) {
                            setErrorInfo(String.format("字段(%s)对应的实体属性(%s)对应节点设置不存在。", fieldName, entityPropertyName));
                        } else {
                            //对名称属性字段进行赋值
                            ele.setNodeValue(node.getNameCodeValue());
                            ele.setFieldValue(node.getNameCodeValue());
                            //标识不需要再校验
                            ele.setIsVerified(1);
                            this.fieldPropertyMap.<Cda26>setEntityValue(entityPropertyName, node.getNameCodeValue(), data);
                        }
                    }
                }
            }
        }
        setErrorInfo(dataVerify.getErrorMessage());
        isOk = (errorMessage.size() == 0);
        if (isOk) {
            dao.update(data);
        }
        return isOk;
    }

    private String cdaCode = "";

    /**
     * 逐个字段验证
     *
     * @param cdaCode   CDA代码
     * @param fieldName 字段名称
     * @param nodeValue 节点值
     * @return true正确，false不正确 请查看errorMessage列表
     */
    public boolean VerifyField(String cdaCode, String fieldName, String nodeValue) {
        boolean isOk;
        if (this.cdaCode != cdaCode) {
            //读取字段与实体属性的映射
            isOk = getCdaMap("C0026");
            if (!isOk) return false;

            listCdaDefine = excel.getListCdaDefine(cdaCode);
            if (listCdaDefine == null) {
                return false;
            }
            this.cdaCode = cdaCode;
        }
        CdaNode node = getCdaNodeByFieldName(fieldName);
        if (node == null) {
            setErrorInfo(String.format("CDA代码(%s)的设置节点中不包含字段(%s)的节点设置。", cdaCode, fieldName));
            return false;
        }
        if (node.getNodePath().isEmpty()) {
            setErrorInfo(String.format("字段(%s)不是CDA代码(%s)的文档要求上传的数据（设置中节点路径为空）。", fieldName, cdaCode));
            return false;
        }
        node.setNodeValue(nodeValue);
        return dataVerify.isNodeOK(node);
    }
}
