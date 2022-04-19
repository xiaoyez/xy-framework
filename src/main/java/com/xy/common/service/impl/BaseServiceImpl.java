package com.xy.common.service.impl;

import cn.hutool.core.util.ReflectUtil;
import com.xy.common.exception.MethodNotFoundException;
import com.xy.common.factory.ConditionBeanFactory;
import com.xy.common.mapper.IBatchDeleteMapper;
import com.xy.common.mapper.IBatchSelectMapper;
import com.xy.common.mapper.Mapper;
import com.xy.common.service.IBaseService;
import com.xy.common.utils.spring.SpringUtils;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import static com.xy.common.utils.ObjectUtils.ignoreEmptyString;

/**
 * 基础Service
 * @author xiaoye
 * @create 2021-09-23 11:24
 */
public class BaseServiceImpl<T, M extends Mapper<T>> implements IBaseService<T, M> {

    @Autowired
    @Getter
    protected M mapper;


    protected String domainClassName;



    @Override
    public T getByPrimaryKey(Object primaryKey) {
        if (primaryKey == null)
            return null;
        return mapper.selectByPrimaryKey(primaryKey);
    }

    @Override
    public T getOne(T domain, boolean ignoreEmptyString) {
        if (domain == null)
            return null;
        if (ignoreEmptyString)
        {
            ignoreEmptyString(domain);
        }
        ConditionBeanFactory.completeDefaultValue(domain);
        return mapper.selectOne(domain);
    }



    @Override
    public List<T> gets(T domain, boolean ignoreEmptyString) {
        if (domain == null)
            return null;
        if (ignoreEmptyString)
        {
            ignoreEmptyString(domain);
        }
        ConditionBeanFactory.completeDefaultValue(domain);
        return mapper.select(domain);
    }

    @Override
    public List<T> getAll() {
        return mapper.selectAll();
    }

    @Override
    public int insert(T domain) {
        if (domain == null)
            return 0;
        return mapper.insertSelective(domain);
    }

    @Override
    public int update(T domain) {
        return update(domain,true);
    }

    @Override
    public int update(T domain, boolean isSelective) {
        if (domain == null)
            return 0;
        if (isSelective)
            return mapper.updateSelective(domain);
        return mapper.updateByPrimaryKey(domain);
    }

    @Override
    public int delete(T domain) {
        if (domain == null)
            return 0;
        return mapper.delete(domain);
    }

    @Override
    public int deleteByPrimaryKey(Object primaryKey) {
        if (primaryKey == null)
            return 0;
        return mapper.deleteByPrimaryKey(primaryKey);
    }

    @Override
    public int deleteByPrimaryKeys(List<?> primaryKeys) {
        if (!CollectionUtils.isEmpty(primaryKeys))
        {
            String domainClassName = getDomainClassName();
            if (mapper instanceof IBatchDeleteMapper)
                return ((IBatchDeleteMapper)mapper).deleteByPrimaryKeys(primaryKeys,domainClassName);
            else
                throw new MethodNotFoundException("未找到方法 deleteByPrimaryKeys,请检查mapper是否继承了 com.xy.common.mapper.IBatchDeleteMapper 接口");
        }
        return 0;
    }

    @Override
    public List<T> getByPrimaryKeys(List<?> ids) {
        if (!CollectionUtils.isEmpty(ids))
        {
            String domainClassName = getDomainClassName();
            if (mapper instanceof IBatchSelectMapper)
                return ((IBatchSelectMapper)mapper).selectByPrimaryKeys(ids,domainClassName);
            else
                throw new MethodNotFoundException("未找到方法 selectByPrimaryKeys,请检查mapper是否继承了 com.xy.common.mapper.IBatchSelectMapper 接口");
        }
        return null;
    }

    public String getDomainClassName()
    {
        if (!StringUtils.hasText(domainClassName))
        {
            ParameterizedType genericSuperclass = (ParameterizedType)this.getClass().getGenericSuperclass();
            Type domainClass = genericSuperclass.getActualTypeArguments()[0];
            domainClassName = domainClass.getTypeName();
        }
        return domainClassName;
    }

    @SneakyThrows
    public boolean isManager(Long userId) {
        Class<?> clazz = Class.forName("com.cgmanage.mapper.system.SysUserRoleTkMapper");
        Object bean = SpringUtils.getBean(clazz);
        Method method = ReflectUtil.getMethod(clazz, "getManagerIds");
        List<Long> managerIds = (List<Long>)method.invoke(bean);
        return managerIds.contains(userId);

    }
}
