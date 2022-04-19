package com.xy.common.mapper.provider;

import com.xy.common.utils.mybatis.MybatisUtils;

import java.util.List;


/**
 * @author xiaoye
 * @create 2021-09-30 15:18
 */
public abstract class AbstractProvider {

    protected String getTableName(Object o) {

        return MybatisUtils.getTableName(o);
    }

    protected String getTableName(Class domainClass)
    {
        return MybatisUtils.getTableName(domainClass);
    }

    protected String buildColumnPattern(String columnName) {
        return MybatisUtils.buildColumnPattern(columnName);
    }

    protected String buildColumnPattern(int i,String columnName) {
        return MybatisUtils.buildColumnPattern(i,columnName);
    }


    protected String getPrimaryKeyName(Class domainClass)
    {
        return MybatisUtils.getPrimaryKeyName(domainClass);
    }

    protected List<String> getColumns(Class<?> domainClass) {
        return MybatisUtils.getColumns(domainClass);
    }

    protected String generatePlaceholder(String placeholder)
    {
        return MybatisUtils.buildColumnPattern(placeholder);
    }
}
