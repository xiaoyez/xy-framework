package com.xy.common.mapper.provider;

import cn.hutool.core.util.StrUtil;
import com.xy.common.utils.ReflectUtils;
import com.xy.common.utils.StringUtils;
import com.xy.common.utils.mybatis.MybatisUtils;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.ibatis.annotations.Param;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author xiaoye
 * @create 2021-09-30 15:13
 */
public class MainTableProvider extends AbstractProvider{

    public String buildSelectSql(Class domainClass)
    {
        List<Class> childTableClasses = getChildTableClasses(domainClass);
        String selectSql = buildSelectSql(domainClass,childTableClasses);
        String fromSql = buildFromSql(domainClass,childTableClasses);
        return selectSql + fromSql;
    }

    private String buildSelectSql(Class domainClass, List<Class> childTableClasses)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("select ");
        String mainTableAlias = getTableAlias(domainClass);
        Map<Class, List<String>> repeatFieldMap = MybatisUtils.getRepeatFieldMap(domainClass, childTableClasses);
        builder.append(buildSelectMainTableColumnsSql(domainClass,mainTableAlias));

        List<String> alias = new ArrayList<>();
        alias.add(mainTableAlias);
        for (Class childTableClass : childTableClasses) {
            String childTableNameAlias = getAlias(childTableClass,alias);
            val columns = getColumns(childTableClass);
            for (String column : columns) {
                builder.append(childTableNameAlias)
                        .append(".")
                        .append(MybatisUtils.getColumnPrefix())
                        .append(column)
                        .append(MybatisUtils.getColumnSuffix())
                        .append(",");
            }
        }
        for (Map.Entry<Class, List<String>> repeatFieldEntry : repeatFieldMap.entrySet()) {
            Class childTableClass = repeatFieldEntry.getKey();
            String childTableNameAlias = getAlias(childTableClass,alias);
            List<String> childTableClassFieldNames = repeatFieldEntry.getValue();

            childTableClassFieldNames.retainAll(ReflectUtils.getFieldNames(childTableClass));
            for (String fieldName : childTableClassFieldNames) {
                String column = StringUtils.toUnderlineCase(fieldName);
                builder.append(childTableNameAlias)
                        .append(".")
                        .append(column)
                        .append(" as ")
                        .append(childTableNameAlias)
                        .append("_")
                        .append(column)
                        .append(",");

            }
        }
        builder.deleteCharAt(builder.length() - 1);
        builder.append(" ");
        return builder.toString();
    }

    private String buildSelectMainTableColumnsSql(Class domainClass, String mainTableAlias) {
        val builder = new StringBuilder();
        List<String> columns = getColumns(domainClass);
        for (String column : columns) {
            builder.append(mainTableAlias)
                    .append(".")
                    .append(MybatisUtils.getColumnPrefix())
                    .append(column)
                    .append(MybatisUtils.getColumnSuffix())
                    .append(",");
        }
        return builder.toString();
    }

    private String getTableAlias(Class domainClass) {
        return StringUtils.getFirstLetters(getTableName(domainClass));
    }

    private String getAlias(Class clazz, List<String> alias) {
        String tableAlias = getTableAlias(clazz);

        for (int i = 1;  alias.contains(tableAlias); i++) {
            tableAlias += i;
        }
        return tableAlias;
    }

    @SneakyThrows
    public String selectCompleteByPrimaryKey(@Param("pk") Object primaryKey,
                                      @Param("className") String className)
    {
        Class<?> domainClass = Class.forName(className);
        String tableName = getTableName(domainClass);
        String pkName = getPrimaryKeyName(domainClass);
        String mainTableNameAlias = StringUtils.getFirstLetters(tableName);
        StringBuilder builder = new StringBuilder();

        builder.append(buildSelectSql(domainClass))
                .append( " where ")
                .append(mainTableNameAlias)
                .append(".")
                .append(pkName)
                .append(" = #{pk}");
        return builder.toString();
    }

    private String buildFromSql(Class<?> domainClass,List<Class> childTableClasses) {
        StringBuilder builder = new StringBuilder();
        String tableName = getTableName(domainClass);
        builder.append(" from ")
                .append(tableName)
                .append(" as ")
                .append(StringUtils.getFirstLetters(tableName))
                .append(" ");

        for (Class childTableClass : childTableClasses) {
            String joinSql = buildJoinSql(domainClass,childTableClass);
            builder.append(joinSql);
        }
        return builder.toString();
    }

    private String buildJoinSql(Class<?> domainClass, Class childTableClass) {
        StringBuilder builder = new StringBuilder();
        String childTableName = getTableName(childTableClass);
        String childTableNameAlias = StringUtils.getFirstLetters(childTableName);
        builder.append("left join ")
                .append(childTableName)
                .append(" as ")
                .append(childTableNameAlias)
                .append(" on ")
                .append(StringUtils.getFirstLetters(getTableName(domainClass)))
                .append(".")
                .append(getMainFk(domainClass))
                .append(" = ")
                .append(childTableNameAlias)
                .append(".")
                .append(getChildFk(childTableClass,domainClass))
                .append(" ");
        return builder.toString();
    }

    private String getChildFk(Class childTableClass, Class<?> domainClass) {
        return MybatisUtils.getChildFk(childTableClass,domainClass);
    }

    private String getMainFk(Class<?> domainClass) {
        return MybatisUtils.getMainFk(domainClass);
    }

    private List<Class> getChildTableClasses(Class<?> domainClass) {
        return MybatisUtils.getChildTableClasses(domainClass);
    }

    public String selectCompleteOne(Object domain)
    {
        Class<?> domainClass = domain.getClass();
        String tableName = getTableName(domainClass);
        String mainTableNameAlias = StringUtils.getFirstLetters(tableName);
        String pkName = getPrimaryKeyName(domainClass);
        List<String> columns = getColumnsHavingValue(domainClass,domain);
        StringBuilder builder = new StringBuilder();
        builder.append(buildSelectSql(domainClass));
        if (!CollectionUtils.isEmpty(columns))
        {
            builder.append(" where ");
            for (String column : columns) {
                builder.append(mainTableNameAlias)
                        .append(".")
                        .append(column)
                        .append(" = ")
                        .append(generatePlaceholder(StrUtil.toCamelCase(column)))
                        .append(" and ");
            }
            builder.delete(builder.lastIndexOf("and"),builder.length());
        }
        return builder.toString();
    }

    public String selectComplete(Object domain)
    {
        return selectCompleteOne(domain);
    }

    @SneakyThrows
    public String selectCompleteAll(@Param("className") String className) {
        Class<?> domainClass = Class.forName(className);
        String tableName = getTableName(domainClass);
        StringBuilder builder = new StringBuilder();
        builder.append(buildSelectSql(domainClass));
        return builder.toString();
    }

    /**
     * 获取有值的列
     * @param domainClass
     * @param domain
     * @return
     */
    private List<String> getColumnsHavingValue(Class<?> domainClass, Object domain) {
        return MybatisUtils.getColumnsHavingValue(domainClass,domain);
    }

}
