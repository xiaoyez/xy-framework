package com.xy.common.service;

import com.xy.common.factory.ConditionBeanFactory;
import com.xy.common.mapper.ISearchAuthMapper;
import com.xy.common.mapper.Mapper;

import java.util.List;

/**
 * @author xiaoye
 * @create 2021-10-13 11:27
 */
public interface ISearchAuthService<T, M extends ISearchAuthMapper<T> & Mapper<T>>
        extends ISearchService<T, M>{

    default List<T> searchAuth(T domain,boolean isAuth)
    {
        if (domain == null)
            return null;
        ConditionBeanFactory.completeDefaultValue(domain);
        if (!isAuth)
            return search(domain);
        return getMapper().searchAuth(domain);
    }
}
