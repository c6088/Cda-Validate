package com.bayc.xsdvalidatecmd.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据库数据源
 */
public class CdaDbHelper {
    /**
     * 驱动
     */
    private String driver = "";
    /**
     * 数据库数据源
     */
    private String dataSource = "";
    /**
     * 数据库用户名
     */
    private String userID = "";
    /**
     * 数据库用户密码
     */
    private String password = "";

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 执行过程中的错误信息列表，没有错误时返回null， 方法执行返回false时，查看此列表
     * 不是返回布尔型时，根据是否返回null，确定是否成功执行。
     */
    public List<String> getErrorMessage() {
        if (errorMessage.size() == 0) return null;
        return errorMessage;
    }

    private List<String> errorMessage = new ArrayList<>();

    private Log log = LogFactory.getLog(CdaDbHelper.class);


    /**
     * 配置文件路径及文件名
     */
    String configFile = "config/Config.xml";

    public String getConfigFile() {
        return configFile;
    }

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    /**
     * 检查配置文件，读取配置文件文件中的数据库连接信息，
     *
     * @return true 成功 false 失败
     */
    public boolean ReadConfigFile() {
        String errorInfo;
        errorMessage.clear();
        java.io.File file = new java.io.File("config/Config.xml");
        if (!file.exists()) {
            errorInfo = configFile + "文件不存在。";
            SetErrorInfo(errorInfo);
            return false;
        }

        log.info("开始读取配置文件" + configFile);
        Document doc;
        SAXReader reader = new SAXReader();
        try {
            doc = reader.read(new java.io.File(configFile));
        } catch (DocumentException e) {
            errorInfo = String.format("SAXReader读文件%s错误：%s", configFile, e.getMessage());
            SetErrorInfo(errorInfo);
            e.printStackTrace();
            return false;
        }

        log.info("判断配置是否正确。");
        int count = 0;
        Element ele;
        String[] infos = new String[]{"DataSource", "UserID", "Password"};
        for (String info :
                infos) {
            String nodePath;
            nodePath = "/Config/Database/" + info;
            ele = (Element) doc.selectSingleNode(nodePath);
            if (ele == null) {
                count++;
                errorMessage.add(String.format("%d: 缺少节点:%s。", count, nodePath));
            }
            String str = ele.getStringValue().trim();
            if (str.isEmpty()) {
                count++;
                errorMessage.add(String.format("%d: 节点:%s不能为空。", count, nodePath));
            }
        }

        if (errorMessage.size() > 0) {
            log.info(configFile + "以下节点错误：");
            for (String info : errorMessage) {
                log.info(info);
            }
            return false;
        }

        log.info("读取数据库配置信息。");
        ele = (Element) doc.selectSingleNode("Config/Database/DataSource");
        String str = ele.getStringValue().trim();
        this.dataSource = "jdbc:oracle:thin:@" + str.replace("/", ":");
        ele = (Element) doc.selectSingleNode("Config/Database/UserID");
        this.userID = ele.getStringValue().trim();
        ele = (Element) doc.selectSingleNode("Config/Database/Password");
        this.password = ele.getStringValue();
        return true;
    }

    private void SetErrorInfo(String errorInfo) {
        errorMessage.add(errorInfo);
        log.info(errorInfo);
    }

    /**
     * 检查MyBatisConfig配置文件，读取配置文件文件中的数据库连接信息，
     *
     * @return true 成功 false 失败
     */
    public boolean ReadMyBatisConfigFile() {
        String errorInfo;
        errorMessage.clear();
        String configFile = "src/main/resources/mybatis-config.xml";
        java.io.File file = new java.io.File(configFile);
        if (!file.exists()) {
            errorInfo = configFile + "文件不存在。";
            SetErrorInfo(errorInfo);
            return false;
        }

        log.info("开始读取配置文件" + configFile);
        Document doc;
        SAXReader reader = new SAXReader();
        try {
            doc = reader.read(new java.io.File(configFile));
        } catch (DocumentException e) {
            errorInfo = String.format("SAXReader读文件%s错误：%s", configFile, e.getMessage());
            SetErrorInfo(errorInfo);
            e.printStackTrace();
            return false;
        }

        log.info("判断配置是否正确。");
        String nodePath = "/configuration/environments/environment/dataSource/property";
        List<Node> listNode = doc.selectNodes(nodePath);
        if (listNode == null || listNode.size() == 0) {
            errorInfo = String.format("%s不存在节点：%s", configFile, nodePath);
            SetErrorInfo(errorInfo);
            return false;
        }
        for (Node node : listNode) {
            Element ele = (Element) node;
            String name = ele.attributeValue("name");
            String value = ele.attributeValue("value");
            if (value.isEmpty()) {
                SetErrorInfo(String.format("%s中没有设置%s", configFile, name));
                continue;
            }
            switch (name) {
                case "driver":
                    this.driver = value;
                    break;
                case "url":
                    this.dataSource = value;
                    break;
                case "username":
                    this.userID = value;
                    break;
                case "password":
                    this.password = value;
                    break;
            }
        }
        return errorMessage.size() == 0;
    }

    /**
     * 取配置文件文件中的数据库连接信息，能否连接到数据库
     *
     * @return true 能 false 不能
     */
    public boolean CanConnectToDb() {
        return Execute("select 1 rc from dual");
    }

    /**
     * 执行SQL语句
     *
     * @param sql 需要执行的SQL语句
     * @return true 成功执行 false 没有执行
     */
    public boolean Execute(String sql) {
        boolean isOk = false;
        errorMessage.clear();
        Statement statement = null;
        Connection conn = null;
        try {
            Class.forName(this.driver);
            conn = DriverManager.getConnection(dataSource, userID, password);
            statement = conn.createStatement();
            statement.execute(sql);
            statement.close();
            statement = null;
            conn.close();
            conn = null;
            isOk = true;
        } catch (Exception e) {
            String errorInfo = String.format("执行语句错误：%s。\nsql=%s", e.getMessage(), sql);
            SetErrorInfo(errorInfo);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (Exception e) {
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {
                }
            }
        }
        return isOk;
    }

    /**
     * 执行SQL语句
     *
     * @param sql 需要执行的SQL语句
     * @return 返回ResultSet，没有数据时，返回null
     */
    public ResultSet getResultSet(String sql) throws SQLException {
        ResultSet rs = null;
        errorMessage.clear();
        Statement statement = null;
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(dataSource, userID, password);
            statement = conn.createStatement();
            statement.executeQuery(sql);
            rs = statement.getResultSet();
            statement.close();
            statement = null;
            conn.close();
            conn = null;
        } catch (Exception e) {
            String errorInfo = String.format("请检查配置文件%s, 不能连接到数据库: %s", configFile, e.getMessage());
            SetErrorInfo(errorInfo);

        } finally {
            if (statement != null) {
                statement.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return rs;
    }

    /**
     * 读取SQL语句第1行1列的整数值
     *
     * @param sql 只返回一行一列的SQL语句，
     * @return 没有取到值时，返回0
     */
    public int getInt(String sql) throws SQLException {
        int v = 0;
        ResultSet rs = this.getResultSet(sql);
        if (rs == null) return 0;
        if (rs.next()) {
            v = rs.getInt(0);
            rs.close();
        }
        return v;
    }

    /**
     * 读取SQL语句第1行1列的字符串值
     *
     * @param sql 只返回一行一列的SQL语句，
     * @return 没有取到值null 取到的值对象
     */
    public String getString(String sql) throws SQLException {
        String v = "";
        ResultSet rs = this.getResultSet(sql);
        if (rs == null) return "";
        if (rs.next()) {
            v = rs.getString(0);
            rs.close();
        }
        return v;
    }
}
