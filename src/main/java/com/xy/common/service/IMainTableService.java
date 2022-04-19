package com.xy.common.service;


import com.xy.common.domain.IMainTableObject;
import com.xy.common.mapper.IMainTableMapper;
import com.xy.common.mapper.Mapper;

import java.util.List;

/**
 * 主表Service
 * @param <T> 主表对应实体类
 * @param <M> 主表对应Mapper
 * @author xiaoye
 * @create 2021-09-23 14:15
 */
public interface IMainTableService<T extends IMainTableObject,
        M extends Mapper<T> & IMainTableMapper<T>
        >
        extends IBaseService<T,M>{

    /**
     * 根据主键获取主表及子表
     * @param primaryKey
     * @return
     */
    T getCompleteByPrimaryKey(Object primaryKey);

    /**
     * 根据条件查主表及子表
     * @param domain
     * @return
     */
    T getCompleteOne(T domain);

    /**
     * 根据条件查主表及子表
     * @param domain
     * @return
     */
    List<T> getsComplete(T domain);

    /**
     * 获取所有的主表及子表
     * @return
     */
    List<T> getCompleteAll();

    /**
     * 新增主表以及子表
     * @param domain
     * @return
     */
    int insertComplete(T domain);

    /**
     * 更新主表以及子表
     * @param domain
     * @return
     */
    int updateComplete(T domain);

    int updateComplete(T domain,boolean isSelective);
}
