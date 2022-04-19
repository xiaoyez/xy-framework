package com.xy.common.service;

import com.xy.common.domain.IMainTableObject;
import com.xy.common.mapper.IAuthMapper;
import com.xy.common.mapper.Mapper;
import lombok.SneakyThrows;

import java.util.List;

/**
 * @author xiaoye
 * @create 2021-10-09 16:25
 */
public interface IAuthService<T, M extends IAuthMapper<T> & Mapper<T> > extends IBaseService<T,M> {


    default List<T> getsAuth(T domain, boolean isAuth)
    {
        if (!isAuth)
            return gets(domain);
        return getMapper().selectAuth(domain);
    }

    @SneakyThrows
    default List<T> getsCompleteAuth(T domain, boolean isAuth)
    {
        if (!isAuth)
        {
            if (this instanceof IMainTableService)
                return ((IMainTableService)this).getsComplete((IMainTableObject) domain);
            else
                throw new NoSuchMethodException("这不是 " + IMainTableService.class.getName() + " 的子类");
        }
        return getMapper().selectCompleteAuth(domain);
    }
}
