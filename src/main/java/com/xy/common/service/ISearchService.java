package com.xy.common.service;

import com.xy.common.factory.ConditionBeanFactory;
import com.xy.common.mapper.ISearchMapper;
import com.xy.common.mapper.Mapper;

import java.util.List;

/**
 * @author xiaoye
 * @create 2021-10-13 11:27
 */
public interface ISearchService<T, M extends ISearchMapper<T> & Mapper<T>>
        extends IBaseService<T, M>{

    default List<T> search(T domain)
    {
        if (domain == null)
            return null;
        ConditionBeanFactory.completeDefaultValue(domain);
        return getMapper().search(domain);
    }
}
