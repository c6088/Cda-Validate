package com.bayc.xsdvalidatecmd;

import com.bayc.xsdvalidatecmd.control.DataConvertVerify;
import com.bayc.xsdvalidatecmd.control.DataExcel;
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
    private static List<CdaNode> listCdaDefine = null;
    private static HashMap<String, String> mapDeDomain = null;
    private static HashMap<String, HashMap<String, String>> mapDicts = null;
    private static HashMap<String, HashMap<String, String>> mapCodeNames = new HashMap<>();

    public static void main(String[] args) {
        //读取数据库配置
        CdaDbHelper dbInfo = new CdaDbHelper();
        boolean isOk = dbInfo.ReadConfigFile();
        if (!isOk) return;
        isOk = dbInfo.CanConnectToDb();
        if (!isOk) return;

        //读取Excel中的数据
        DataExcel excel = new DataExcel();
        mapDicts = excel.getMapDicts();

        isOk = excel.open("");
        if (!isOk) {
            excel.close();
            return;
        }

        List<CdaNode> listCdaDefine = excel.getListCdaDefine();
        int v = listCdaDefine.size();
        mapDeDomain = excel.getDeDomain();
        v = mapDeDomain.size();
        excel.close();


        try {
            isOk = ValidateC0026("10039844_1_1");
        } catch (Exception ex) {
            log.info(ex.getMessage());
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
     * 验证手术知情同意书
     *
     * @param informedConsentNo 手术知情同意书单号
     * @return true正确 false有错误
     */
    public static boolean ValidateC0026(String informedConsentNo) throws IOException {
        log.info("读取相对路径下的mybatis.xml配置文件");
        Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
        SqlSessionFactory ssf = new SqlSessionFactoryBuilder().build(reader);
        SqlSession sqlSession = ssf.openSession(true);
        Cda26Dao dao = sqlSession.getMapper(Cda26Dao.class);
        Cda26 data = dao.queryById(informedConsentNo);
        errorMessage.clear();
        HashMap<String, String> mapCodeNames = new HashMap<>();

        DataConvertVerify dataVerify = new DataConvertVerify();
        boolean isModified = false;
        for (CdaNode node : listCdaDefine) {
            if (!dataVerify.isNodeOK(node)) {
                errorMessage.add(dataVerify.getInfoMessage());
            }
            if (!node.getNameCodeField().isEmpty()) {
                isModified = true;
                //通过Map找到字段， 然后用反射将值写入
            }
        }
        boolean isOk = (errorMessage.size() == 0);
        if (isOk && isModified) {
            dao.update(data);
        }
        return isOk;
    }


}
