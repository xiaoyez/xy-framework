package com.xy.common.mapper.provider;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.xy.common.domain.annotation.OrderBy;
import com.xy.common.domain.annotation.search.*;
import com.xy.common.utils.StringUtils;
import com.xy.common.utils.mybatis.MybatisUtils;
import lombok.val;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xiaoye
 * @create 2021-10-13 11:08
 */
public class SearchProvider extends AbstractProvider{

    public String search(Object domain)
    {
        Class<?> domainClass = domain.getClass();
        val fields = MybatisUtils.getEffectiveField(domainClass);
        List<Field> conditionFields = fields.stream()
                .filter(field -> {
                    return AnnotationUtil.hasAnnotation(field, Condition.class);
                })
                .filter(field -> {
                    return StringUtils.getDefaultNull(ReflectUtil.getFieldValue(domain,field)) != null;
                })
                .collect(Collectors.toList());
        List<Field> orderByFields = fields.stream()
                .filter(field -> {
                    return AnnotationUtil.hasAnnotation(field, OrderBy.class);
                })
                .collect(Collectors.toList());
        StringBuilder builder = new StringBuilder();
        builder.append(MybatisUtils.buildSelectSql(domainClass))
                .append(" ")
                .append(generateConditionSql(conditionFields))
                .append(generateOrderBySql(orderByFields));
        return builder.toString();
    }

    private String generateOrderBySql(List<Field> orderByFields) {
        if (CollectionUtils.isEmpty(orderByFields))
            return "";
        StringBuilder builder = new StringBuilder();
        builder.append(" order by ");
        for (Field field : orderByFields) {
            String columnName = StringUtils.toUnderlineCase(field.getName());
            builder.append(columnName)
                    .append(" ")
                    .append(AnnotationUtil.getAnnotation(field,OrderBy.class).value())
                    .append(",");
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    public String searchAuth(Object domain)
    {
        String sql = search(domain);
        String orderBySql = "";
        final String ORDER_BY = "order by";
        int index = sql.indexOf(ORDER_BY);
        if (index > 0) {
            String[] split = sql.split(ORDER_BY);
            sql = split[0];
            orderBySql = ORDER_BY + split[1];
        }
        Class<?> domainClass = domain.getClass();
        String pkName = getPrimaryKeyName(domainClass);
        StringBuilder builder = new StringBuilder();
        builder.append(sql);
        String authQuerySql = StringUtils.getDefaultEmpty(ReflectUtil.getFieldValue(domain, "authQuerySql"));
        if (sql.contains("where"))
            builder.append(" and ");
        else
            builder.append(" where ");
        builder.append(pkName)
                .append(" in ( ")
                .append(authQuerySql)
                .append(" )\n");
        builder.append(orderBySql);
        return builder.toString();
    }

    private String generateConditionSql(List<Field> conditionFields) {
        if (CollectionUtils.isEmpty(conditionFields))
            return "";
        StringBuilder builder = new StringBuilder();
        builder.append(" where \n");
        List<String> conditionSqls = new ArrayList<>();
        for (Field conditionField : conditionFields) {
            conditionSqls.add(generateConditionSql(conditionField));
        }
        builder.append(StrUtil.join(" and ",conditionSqls));
        return builder.toString();
    }

    private String generateConditionSql(Field conditionField) {
        StringBuilder builder = new StringBuilder();
        if (AnnotationUtil.hasAnnotation(conditionField, Like.class))
        {
            builder.append(generateLikeSql(conditionField));
        }
        else if (AnnotationUtil.hasAnnotation(conditionField, NotEqual.class))
        {
            builder.append(generateNotEqualSql(conditionField));
        }
        else if (AnnotationUtil.hasAnnotation(conditionField, Equal.class))
        {
            builder.append(generateEqualSql(conditionField));
        }
        else if (AnnotationUtil.hasAnnotation(conditionField, GreaterThan.class))
        {
            builder.append(generateGreaterThanSql(conditionField));
        }
        else if (AnnotationUtil.hasAnnotation(conditionField, GreaterEqual.class))
        {
            builder.append(generateGreaterEqualSql(conditionField));
        }
        else if (AnnotationUtil.hasAnnotation(conditionField, LessThan.class))
        {
            builder.append(generateLessThanSql(conditionField));
        }
        else if (AnnotationUtil.hasAnnotation(conditionField, LessEqual.class))
        {
            builder.append(generateLessEqualSql(conditionField));
        }
        else
        {
            builder.append(generateEqualSql(conditionField));
        }
        return builder.toString();
    }

    private String generateLessEqualSql(Field conditionField) {
        StringBuilder builder = new StringBuilder();
        String name = conditionField.getName();
        builder.append(MybatisUtils.getColumnName(conditionField))
                .append(" <= ")
                .append(generatePlaceholder(name))
                .append("\n");
        return builder.toString();
    }

    private String generateLessThanSql(Field conditionField) {
        StringBuilder builder = new StringBuilder();
        String name = conditionField.getName();
        builder.append(MybatisUtils.getColumnName(conditionField))
                .append(" < ")
                .append(generatePlaceholder(name))
                .append("\n");
        return builder.toString();
    }

    private String generateGreaterEqualSql(Field conditionField) {
        StringBuilder builder = new StringBuilder();
        String name = conditionField.getName();
        builder.append(MybatisUtils.getColumnName(conditionField))
                .append(" >= ")
                .append(generatePlaceholder(name))
                .append("\n");
        return builder.toString();
    }

    private String generateGreaterThanSql(Field conditionField) {
        StringBuilder builder = new StringBuilder();
        String name = conditionField.getName();
        builder.append(MybatisUtils.getColumnName(conditionField))
                .append(" > ")
                .append(generatePlaceholder(name))
                .append("\n");
        return builder.toString();
    }

    private String generateNotEqualSql(Field conditionField) {
        StringBuilder builder = new StringBuilder();
        String name = conditionField.getName();
        builder.append(MybatisUtils.getColumnName(conditionField))
                .append(" != ")
                .append(generatePlaceholder(name))
                .append("\n");
        return builder.toString();
    }

    private String generateEqualSql(Field conditionField) {
        StringBuilder builder = new StringBuilder();
        String name = conditionField.getName();
        builder.append(MybatisUtils.getColumnName(conditionField))
                .append(" = ")
                .append(generatePlaceholder(name))
                .append("\n");
        return builder.toString();
    }

    private String generateLikeSql(Field conditionField) {
        Like like = AnnotationUtil.getAnnotation(conditionField, Like.class);
        StringBuilder builder = new StringBuilder();
        String name = conditionField.getName();
        builder.append(MybatisUtils.getColumnName(conditionField))
                .append(" like concat('")
                .append(like.prefix())
                .append("',")
                .append(generatePlaceholder(name))
                .append(",'")
                .append(like.suffix())
                .append("')\n");
        return builder.toString();
    }
}
