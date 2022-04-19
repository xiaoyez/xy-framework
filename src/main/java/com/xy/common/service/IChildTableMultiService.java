package com.xy.common.service;


import com.xy.common.domain.IChildTableMultiObject;
import com.xy.common.mapper.IChildTableMultiMapper;
import com.xy.common.mapper.Mapper;

import java.util.List;

/**
 * 一对多子表Service
 * @param <T> 一对多子表对应实体类
 * @param <M> 一对多子表对应Mapper
 * @author xiaoye
 * @create 2021-09-23 16:02
 */
public interface IChildTableMultiService<T extends IChildTableMultiObject, M extends Mapper<T> & IChildTableMultiMapper<T>>
        extends IBatchService<T,M>{

    int batchUpdate(List<T> oldList, List<T> newList);

    int batchUpdate(List<T> oldList, List<T> newList,boolean isSelective);


}
