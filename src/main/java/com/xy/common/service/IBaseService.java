package com.xy.common.service;

import com.xy.common.mapper.Mapper;

import java.util.List;

/**
 * 基础Service
 * @param <T> 实体类
 * @param <M> 实体类对应的TkMapper类
 * @author xiaoye
 * @create 2021-09-23 11:24
 */
public interface IBaseService <T, M extends Mapper<T>> {

    M getMapper();

    String getDomainClassName();

    // 根据主键获取对象
    T getByPrimaryKey(Object primaryKey);

    // 条件查询获取对象
    default T getOne(T domain)
    {
        return getOne(domain,true);
    }

    T getOne(T domain, boolean ignoreEmptyString);

    // 条件查询获取对象集合
    default List<T> gets(T domain)
    {
        return gets(domain,true);
    }

    List<T> gets(T domain, boolean ignoreEmptyString);

    // 获取所有对象集合
    List<T> getAll();

    // 插入对象
    int insert(T domain);

    // 修改对象
    int update(T domain);

    // 修改对象
    int update(T domain, boolean isSelective);

    // 条件查询删除对象
    int delete(T domain);

    // 根据主键删除对象
    int deleteByPrimaryKey(Object primaryKey);

    // 根据主键批量删除对象
    int deleteByPrimaryKeys(List<?> primaryKeys);

    List<T> getByPrimaryKeys(List<?> ids);

}
