package com.xy.common.service;

import com.xy.common.mapper.IBatchMapper;
import org.springframework.util.CollectionUtils;
import com.xy.common.mapper.Mapper;

import java.util.List;

/**
 * @author xiaoye
 * @create 2021-10-08 16:53
 */
public interface IBatchService <T, M extends Mapper<T> & IBatchMapper<T>>
    extends IBaseService<T,M> {


    default int batchInsert(List<T> list) {
        if (CollectionUtils.isEmpty(list))
            return 0;
        return getMapper().batchInsert(list);
    }


    default int batchUpdate(List<T> list) {
        if (CollectionUtils.isEmpty(list))
            return 0;
        return getMapper().batchUpdate(list);
    }

    default int batchUpdate(List<T> list, boolean isSelective){
        if (CollectionUtils.isEmpty(list))
            return 0;
        return getMapper().batchUpdate(list);
    }
}
