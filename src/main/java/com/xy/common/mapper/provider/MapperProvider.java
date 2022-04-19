package com.xy.common.mapper.provider;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.ReflectUtil;
import com.xy.common.domain.annotation.Force;
import com.xy.common.utils.mybatis.MybatisUtils;
import lombok.val;

import java.lang.reflect.Field;

/**
 * @author xiaoye
 * @create 2021-11-04 11:39
 */
public class MapperProvider extends AbstractProvider {

    public String updateSelective(Object domain)
    {
        String tableName = getTableName(domain);
        Class<?> domainClass = domain.getClass();
        String pkName = getPrimaryKeyName(domainClass);

        StringBuilder builder = new StringBuilder();
        builder.append("update ")
                .append(tableName)
                .append(" set ")
                .append(buildSetExprs(domain))
                .append(" where ")
                .append(pkName)
                .append(" = ")
                .append(buildColumnPattern(pkName));
        return builder.toString();
    }

    private String buildSetExprs(Object elem)
    {
        return buildSetExprs(elem,true);
    }

    private String buildSetExprs(Object elem, boolean isSelective) {
        final val fields = MybatisUtils.getEffectiveField(elem.getClass());
        StringBuilder builder = new StringBuilder();
        Class<?> domainClass = elem.getClass();
        if (MybatisUtils.getDbType().equals(MybatisUtils.DbType.SQLSERVER))
        {
            final val pkField = MybatisUtils.getPrimaryKeyField(domainClass);
            fields.remove(pkField);
        }
        for (Field field : fields) {
            Object fieldValue = ReflectUtil.getFieldValue(elem, field);
            boolean isForce = AnnotationUtil.hasAnnotation(field, Force.class);
            if (isSelective && !isForce && fieldValue == null)
                continue;
            String column = MybatisUtils.getColumnName(field);
            builder.append(MybatisUtils.getColumnPrefix())
                    .append(column)
                    .append(MybatisUtils.getColumnSuffix())
                    .append(" = ")
                    .append(buildColumnPattern(column))
                    .append(",");
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

}
