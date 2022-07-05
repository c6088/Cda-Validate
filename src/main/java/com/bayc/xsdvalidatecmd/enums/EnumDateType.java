package com.bayc.xsdvalidatecmd.enums;

/**
 * 节点数据类型枚举
 */
public enum EnumDateType {
    /**
     * 可见字符型
     */
    S1,
    /**
     * 字典类型
     */
    S2,
    /**
     * 枚举类型
     */
    S3,
    /**
     * 逻辑类型 true, false
     */
    L,
    /**
     * 日期类型 D8
     */
    D,
    /**
     * 日期时间类型 DT14
     */
    DT,
    /**
     * 时间类型T6
     */
    T,
    /**
     * 字符类型
     */
    A,
    /**
     * 数值类型 整数与实数
     */
    N,
    /**
     * 数据类型 整数与实数
     */
    AN,
    /**
     * 二进制类型BY， BINARY
     */
    BLOB
}