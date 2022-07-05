package com.bayc.xsdvalidatecmd.dao;

import com.bayc.xsdvalidatecmd.entity.Cda26;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 手术知情告知书(Cda26)表数据库访问层
 *
 * @author makejava
 * @since 2022-07-04 15:14:52
 */
public interface Cda26Dao {

    /**
     * 通过ID查询单条数据
     *
     * @param informedConsentNo 主键
     * @return 实例对象
     */
    Cda26 queryById(String informedConsentNo);

    /**
     * 统计总行数
     *
     * @param cda26 查询条件
     * @return 总行数
     */
    long count(Cda26 cda26);

    /**
     * 新增数据
     *
     * @param cda26 实例对象
     * @return 影响行数
     */
    int insert(Cda26 cda26);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<Cda26> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<Cda26> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<Cda26> 实例对象列表
     * @return 影响行数
     */
    int insertOrUpdateBatch(@Param("entities") List<Cda26> entities);

    /**
     * 修改数据
     *
     * @param cda26 实例对象
     * @return 影响行数
     */
    int update(Cda26 cda26);

    /**
     * 通过主键删除数据
     *
     * @param informedConsentNo 主键
     * @return 影响行数
     */
    int deleteById(String informedConsentNo);

}

