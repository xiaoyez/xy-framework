package com.xy.common.utils.mybatis;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.TypeUtil;
import com.xy.common.domain.IChildTableMultiObject;
import com.xy.common.domain.IChildTableSingleObject;
import com.xy.common.domain.annotation.ChildFK;
import com.xy.common.domain.annotation.MainRefKey;
import com.xy.common.domain.annotation.MultiChildTableField;
import com.xy.common.domain.annotation.SingleChildTableField;
import com.xy.common.utils.StringUtils;
import com.xy.common.utils.mybatis.definition.resultmap.*;
import com.xy.common.utils.spring.SpringUtils;
import lombok.val;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author xiaoye
 * @create 2021-10-21 9:55
 */
public class MybatisUtils {

    public static String buildResultMap(Class mainTableClass)
    {
        ResultMapDefinition resultMapDefinition = new ResultMapDefinition();
        String resultMapId = buildResultMapId(mainTableClass);
        resultMapDefinition.setId(resultMapId)
                .setType(mainTableClass)
                .setAutoMapping(true);
        IdDefinition idDefinition = new IdDefinition();
        String pkName = getPrimaryKeyName(mainTableClass);
        idDefinition.setProperty(StringUtils.firstLetterToLowerCase(StringUtils.toCamelCase(pkName)))
                .setColumn(pkName);
        resultMapDefinition.addId(idDefinition);

        List<Class> childTableClasses = getChildTableClasses(mainTableClass);
        Map<Class,String> childTableClassMap = getChildTableClassMap(mainTableClass);
        Map<Class, List<String>> repeatFieldMap = getRepeatFieldMap(mainTableClass, childTableClasses);
        for (Map.Entry<Class, List<String>> repeatFieldClassEntry : repeatFieldMap.entrySet()) {
            Class childTableClass = repeatFieldClassEntry.getKey();
            List<String> fields = repeatFieldClassEntry.getValue();
            String property = childTableClassMap.get(childTableClass);
            if (IChildTableSingleObject.class.isAssignableFrom(childTableClass))
            {
                AssociationDefinition associationDefinition = buildAssociation(property,childTableClass,fields);
                resultMapDefinition.addAssociation(associationDefinition);
            }
            else if (IChildTableMultiObject.class.isAssignableFrom(childTableClass))
            {
                CollectionDefinition collectionDefinition = buildCollection(property,childTableClass,fields);
                resultMapDefinition.addCollection(collectionDefinition);
            }
        }
        return resultMapDefinition.toXmlString();
    }

    private static CollectionDefinition buildCollection(String property,Class childTableClass,List<String> fields) {
        CollectionDefinition collectionDefinition = new CollectionDefinition();
        collectionDefinition.setProperty(property)
                .setOfType(childTableClass)
                .setAutoMapping(true);
        String childTableAlias = StringUtils.getFirstLetters(StringUtils.toUnderlineCase(childTableClass.getSimpleName()));
        for (String fieldName : fields) {
            ResultDefinition resultDefinition = new ResultDefinition();
            resultDefinition.setProperty(fieldName)
                    .setColumn(childTableAlias + "_" + StringUtils.toUnderlineCase(fieldName));
            collectionDefinition.addResult(resultDefinition);
        }
        return collectionDefinition;
    }

    private static AssociationDefinition buildAssociation(String property,Class childTableClass,List<String> fields) {
        AssociationDefinition associationDefinition = new AssociationDefinition();
        associationDefinition.setProperty(property)
                .setJavaType(childTableClass)
                .setAutoMapping(true);
        String childTableAlias = StringUtils.getFirstLetters(StringUtils.toUnderlineCase(childTableClass.getSimpleName()));
        for (String fieldName : fields) {
            ResultDefinition resultDefinition = new ResultDefinition();
            resultDefinition.setProperty(fieldName)
                    .setColumn(childTableAlias + "_" + StringUtils.toUnderlineCase(fieldName));
            associationDefinition.addResult(resultDefinition);
        }
        return associationDefinition;
    }

    private static String buildResultMapId(Class mainTableClass) {
        String tableName = getTableName(mainTableClass);
        String resultMapId = tableName.substring(tableName.indexOf("_")+1);
        resultMapId = resultMapId.substring(resultMapId.indexOf("_")+1);
        resultMapId = StringUtils.toCamelCase(resultMapId) + "ResultVo";
        return resultMapId;
    }

    public static String getTableName(Object o) {

        Class<?> domainClass = o.getClass();
        return getTableName(domainClass);
    }

    public static String buildColumnPattern(int i, String columnName) {
        StringBuilder builder = new StringBuilder();
        builder.append("#{list[")
                .append(i)
                .append("].")
                .append(StringUtils.firstLetterToLowerCase(StringUtils.toCamelCase(columnName)))
                .append("} ");
        return builder.toString();
    }


    public static String getPrimaryKeyName(Class domainClass)
    {
        Field[] fields = ReflectUtil.getFields(domainClass);
        for (Field field : fields) {
            if (AnnotationUtil.getAnnotation(field, Id.class) != null)
                return getColumnName(field);
        }
        return null;
    }
    public static Field getPrimaryKeyField(Class domainClass)
    {
        Field[] fields = ReflectUtil.getFields(domainClass);
        for (Field field : fields) {
            if (AnnotationUtil.getAnnotation(field, Id.class) != null)
                return field;
        }
        return null;
    }


    public static List<String> getColumns(Class<?> domainClass) {
        return getEffectiveField(domainClass).stream()
                .map(field -> getColumnName(field))
                .collect(Collectors.toList());
    }

    public static String getChildFk(Class childTableClass, Class<?> domainClass) {
        List<Field> fields = Arrays.stream(ReflectUtil.getFields(childTableClass))
                .filter(field -> {
                    return AnnotationUtil.hasAnnotation(field, ChildFK.class);
                }).collect(Collectors.toList());
        Field field;
        if (fields.size() > 1)
        {
            field = fields.stream().filter(field1 -> {
                return AnnotationUtil.getAnnotation(field1,ChildFK.class).mainTableClass() == domainClass;
            }).collect(Collectors.toList()).get(0);
        }
        else
            field = fields.get(0);
        return StringUtils.toUnderlineCase(field.getName());
    }

    public static String getMainFk(Class<?> domainClass) {
        List<Field> fieldList = Arrays.stream(ReflectUtil.getFields(domainClass))
                .filter(field -> {
                    return AnnotationUtil.hasAnnotation(field, MainRefKey.class);
                })
                .collect(Collectors.toList());
        return StringUtils.toUnderlineCase(fieldList.get(0).getName());
    }

    public static List<Class> getChildTableClasses(Class<?> domainClass) {
        List<Class> childTableClasses = Arrays.stream(ReflectUtil.getFields(domainClass))
                .filter(field -> {
                    return AnnotationUtil.hasAnnotation(field, SingleChildTableField.class)
                            || AnnotationUtil.hasAnnotation(field, MultiChildTableField.class);
                })
                .map(field -> {
                    if (field.getType().isAssignableFrom(List.class))
                    {
                        Type genericType = field.getGenericType();
                        Type typeArgument = TypeUtil.getTypeArgument(genericType);
                        return (Class)typeArgument;
                    }
                    return field.getType();
                })
                .collect(Collectors.toList());
        return childTableClasses;
    }

    public static Map<Class,String> getChildTableClassMap(Class<?> domainClass)
    {
        Map<Class,String> childTableClassMap = new HashMap<>();
        Arrays.stream(ReflectUtil.getFields(domainClass))
                .filter(field -> {
                    return AnnotationUtil.hasAnnotation(field, SingleChildTableField.class)
                            || AnnotationUtil.hasAnnotation(field, MultiChildTableField.class);
                })
                .forEach(field -> {
                    Class childTableClass;
                    if (field.getType().isAssignableFrom(List.class))
                    {
                        Type genericType = field.getGenericType();
                        Type typeArgument = TypeUtil.getTypeArgument(genericType);
                        childTableClass =  (Class)typeArgument;
                    }
                    else
                    {
                        childTableClass = field.getType();
                    }
                    childTableClassMap.put(childTableClass,field.getName());
                });
        return childTableClassMap;
    }

    /**
     * 获取有值的列
     * @param domainClass
     * @param domain
     * @return
     */
    public static List<String> getColumnsHavingValue(Class<?> domainClass, Object domain) {
        List<String> list = Arrays.stream(ReflectUtil.getFields(domainClass))
                .filter((field) -> {
                    return ReflectUtil.getFieldValue(domain, field) != null;
                })
                .filter(field -> {
                    return !AnnotationUtil.hasAnnotation(field, Transient.class);
                })
                .map((field) -> {
                    return StringUtils.toUnderlineCase(field.getName());
                })
                .collect(Collectors.toList());
        return list;
    }

    public static Map<Class,List<String>> getRepeatFieldMap(Class mainTableClass,List<Class> childTableClasses)
    {
        Map<String, Class> map = Arrays.stream(ReflectUtil.getFields(mainTableClass))
                .filter(field -> {
                    return !AnnotationUtil.hasAnnotation(field, Transient.class);
                })
                .collect(Collectors.toMap(
                        i -> i.getName(),
                        i -> mainTableClass
                ));
        Map<Class,List<String>> repeatFieldMap = new LinkedHashMap<>();
        for (Class childTableClass : childTableClasses) {
            Arrays.stream(ReflectUtil.getFields(childTableClass))
                    .filter(field -> {
                        return !AnnotationUtil.hasAnnotation(field, Transient.class);
                    })
                    .forEach(field -> {
                        if (map.get(field.getName()) == null)
                            map.put(field.getName(),childTableClass);
                        else
                        {
                            List<String> fields = repeatFieldMap.get(childTableClass);
                            if (fields  == null)
                            {
                                fields = new ArrayList<>();
                                repeatFieldMap.put(childTableClass,fields);
                            }
                            fields.add(field.getName());
                        }
                    });
        }
        return repeatFieldMap;
    }

    public static String buildColumnPattern(String columnName) {
        StringBuilder builder = new StringBuilder();
        builder.append("#{")
                .append(StringUtils.firstLetterToLowerCase(StringUtils.toCamelCase(columnName)))
                .append("} ");
        return builder.toString();
    }

    public static String getColumnName(Field field) {
        if(AnnotationUtil.hasAnnotation(field, Column.class))
        {
            Column column = AnnotationUtil.getAnnotation(field, Column.class);
            String name = column.name();
            if (StringUtils.hasText(name))
                return name;
        }
        return StringUtils.toUnderlineCase(field.getName());
    }

    public static String getTableName(Class clazz)
    {
        if(AnnotationUtil.hasAnnotation(clazz, Table.class))
        {
            Table table = AnnotationUtil.getAnnotation(clazz, Table.class);
            String name = table.name();
            if (StringUtils.hasText(name))
                return name;
        }
        return StringUtils.toUnderlineCase(clazz.getSimpleName());
    }

    /**
     * 获取有效的属性，该属性不被@Transient注解标注也不被transient关键字修饰
     * @param clazz
     * @return
     */
    public static List<Field> getEffectiveField(Class clazz)
    {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> {
                    return !AnnotationUtil.hasAnnotation(field,Transient.class)
                            && !(Modifier.isTransient(field.getModifiers()));
                })
                .collect(Collectors.toList());
    }

    public static String buildSelectSql(Class clazz)
    {
        return buildSelectSql(clazz,null);
    }

    public static String buildSelectSql(Class clazz,String tableAlias)
    {
        val builder = new StringBuilder();
        builder.append("select ");
        builder.append(buildSelectColumnsSql(clazz,tableAlias));
        builder.append(" from ")
                .append(getTableName(clazz));
        if (StringUtils.hasText(tableAlias))
            builder.append(" as ")
                    .append(tableAlias);
        return builder.toString();

    }

    private static String buildSelectColumnsSql(Class clazz) {
        return buildSelectColumnsSql(clazz,null);
    }

    public static String buildSelectColumnsSql(Class clazz, String tableAlias) {
        val builder = new StringBuilder();
        val fields = getEffectiveField(clazz);
        String columnPrefix = StringUtils.hasText(tableAlias)?tableAlias+".":"";
        String columnAliasPrefix = StringUtils.hasText(tableAlias)?tableAlias+"_":"";
        for (Field field : fields) {
            builder.append(columnPrefix + getColumnName(field))
                    .append(" as ")
                    .append(columnAliasPrefix + field.getName())
                    .append(",");
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    public static String getDbType()
    {
        try{
            return SpringUtils.getBean(DataSource.class)
                    .getConnection()
                    .getMetaData()
                    .getDatabaseProductName()
                    .toUpperCase();
        }catch (Exception e) {
            return DbType.MYSQL;
        }
    }

    private static String columnPrefix = null;
    private static String columnSuffix = null;
    private static Map<String,String> columnPrefixMap = new HashMap<String,String>(){{
        put(DbType.MYSQL,"`");
        put(DbType.SQLSERVER,"[");
    }};

    private static Map<String,String> columnSuffixMap = new HashMap<String,String>(){{
        put(DbType.MYSQL,"`");
        put(DbType.SQLSERVER,"]");
    }};

    public static String getColumnPrefix() {
        if (columnPrefix == null)
        {
            columnPrefix = columnPrefixMap.get(getDbType());
        }
        return StringUtils.getDefaultEmpty(columnPrefix);
    }

    public static String getColumnSuffix() {
        if (columnSuffix == null)
        {
            columnSuffix = columnSuffixMap.get(getDbType());
        }
        return StringUtils.getDefaultEmpty(columnSuffix);
    }

    public static class DbType{
        public static final String
            MYSQL = "MYSQL",
            SQLSERVER = "MICROSOFT SQL SERVER";
    }
}
