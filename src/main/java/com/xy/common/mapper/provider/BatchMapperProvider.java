package com.xy.common.mapper.provider;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.xy.common.utils.mybatis.MybatisUtils;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.ibatis.annotations.Param;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author xiaoye
 * @create 2021-09-24 11:12
 */
public class BatchMapperProvider extends AbstractProvider{

    public String batchInsert(@Param("list") List<?> list)
    {
        StringBuilder builder = new StringBuilder();
        String tableName = getTableName(list.get(0));
        builder.append("INSERT INTO ")
                .append(tableName)
                .append("\n")
                .append("(");
        Class domainClass = list.get(0).getClass();
        List<String> columns = getColumns(domainClass);
        if(MybatisUtils.getDbType().equals(MybatisUtils.DbType.SQLSERVER))
        {
            final val pkName = getPrimaryKeyName(domainClass);
            columns.remove(pkName);
        }
        String sqlColumns = StrUtil.join(",",columns);
        builder.append(sqlColumns)
                .append(")")
                .append(" values \n");

        for(int i=0;i<list.size();i++){
            builder.append(buildPattern(columns,i));
            if (i < list.size() - 1) {
                builder.append(",\n");
            }
        }
        return builder.toString();
    }

    public String batchUpdateSelective(@Param("list") List<?> list)
    {
        StringBuilder builder = new StringBuilder();
        String tableName = getTableName(list.get(0));
        Class domainClass = list.get(0).getClass();
        List<String> columns = getColumns(domainClass);
        String primaryKeyName = getPrimaryKeyName(domainClass);
        if (!StringUtils.hasText(primaryKeyName))
            return "";
        columns.remove(primaryKeyName);
        for (int i = 0; i < list.size(); i++) {
            Object elem = list.get(i);
            if (elem == null)
                continue;
            builder.append("update ")
                    .append(tableName)
                    .append(" set ")
                    .append(buildSetExprs(i,elem,columns))
                    .append(" where ")
                    .append(primaryKeyName)
                    .append(" = ")
                    .append(buildColumnPattern(i,primaryKeyName))
                    .append(";\n");
        }
        System.out.println(builder.toString());
        return builder.toString();
    }

    public String batchUpdate(@Param("list") List<?> list)
    {
        StringBuilder builder = new StringBuilder();
        String tableName = getTableName(list.get(0));
        Class domainClass = list.get(0).getClass();
        List<String> columns = getColumns(domainClass);
        String primaryKeyName = getPrimaryKeyName(domainClass);
        if (!StringUtils.hasText(primaryKeyName))
            return "";
        columns.remove(primaryKeyName);
        for (int i = 0; i < list.size(); i++) {
            Object elem = list.get(i);
            if (elem == null)
                continue;
            builder.append("update ")
                    .append(tableName)
                    .append(" set ")
                    .append(buildSetExprs(i,elem,columns,false))
                    .append(" where ")
                    .append(primaryKeyName)
                    .append(" = ")
                    .append(buildColumnPattern(i,primaryKeyName))
                    .append(";\n");
        }
        System.out.println(builder.toString());
        return builder.toString();
    }

    @SneakyThrows
    public String deleteByPrimaryKeys(@Param("list") List<?> primaryKeys, @Param("className") String className)
    {
        StringBuilder builder = new StringBuilder();
        Class<?> domainClass = Class.forName(className);
        builder.append("delete from ")
                .append(getTableName(domainClass))
                .append(" where ")
                .append(getPrimaryKeyName(domainClass))
                .append(" in (")
                .append(buildInExpr(primaryKeys))
                .append(")");
        return builder.toString();
    }

    @SneakyThrows
    public String selectByPrimaryKeys(@Param("list") List<?> ids, @Param("className") String className)
    {
        StringBuilder builder = new StringBuilder();
        Class<?> domainClass = Class.forName(className);
        builder.append("select * from ")
                .append(getTableName(domainClass))
                .append(" where ")
                .append(getPrimaryKeyName(domainClass))
                .append(" in (")
                .append(buildInExpr(ids))
                .append(")");
        return builder.toString();
    }

    private String buildInExpr(List<?> primaryKeys) {
        int size = primaryKeys.size();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < size; i++) {
            builder.append("#{list[")
                    .append(i)
                    .append("]},");
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    private String buildSetExprs(int i, Object elem, List<String> columns)
    {
        return buildSetExprs(i,elem,columns,true);
    }

    private String buildSetExprs(int i,Object elem,List<String> columns, boolean isSelective) {
        StringBuilder builder = new StringBuilder();
        for (String column : columns) {
            String fieldName = StrUtil.toCamelCase(column);
            if (isSelective)
                if (ReflectUtil.getFieldValue(elem, fieldName) == null)
                    continue;
            builder.append(MybatisUtils.getColumnPrefix())
                    .append(column)
                    .append(MybatisUtils.getColumnSuffix())
                    .append(" = ")
                    .append(buildColumnPattern(i,column))
                    .append(",");
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }


    private String buildPattern(List<String> columns, int i) {
        StringBuilder builder = new StringBuilder();
        builder.append("(");
        for (String column : columns) {
            builder.append(buildColumnPattern(i,column))
                    .append(",");
        }
        builder.deleteCharAt(builder.length() - 1);
        builder.append(")");
        return builder.toString();
    }

}
