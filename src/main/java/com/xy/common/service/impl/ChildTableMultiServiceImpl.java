package com.xy.common.service.impl;


import com.xy.common.domain.IChildTableMultiObject;
import com.xy.common.mapper.IChildTableMultiMapper;
import com.xy.common.service.IChildTableMultiService;
import com.xy.common.utils.ObjectUtils;
import org.springframework.util.CollectionUtils;
import com.xy.common.mapper.Mapper;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 一对多子表Service
 * @author xiaoye
 * @create 2021-09-23 16:03
 */
public abstract class ChildTableMultiServiceImpl<T extends IChildTableMultiObject, M extends Mapper<T> & IChildTableMultiMapper<T>>
    extends BaseServiceImpl<T,M>
    implements IChildTableMultiService<T,M> {

    @Override
    public int batchUpdate(List<T> oldList, List<T> newList) {
        return batchUpdate(oldList,newList,true);
    }

    @Override
    public int batchUpdate(List<T> oldList, List<T> newList, boolean isSelective) {
        if (CollectionUtils.isEmpty(oldList)
                && CollectionUtils.isEmpty(newList))
            return 0;
        if (CollectionUtils.isEmpty(oldList)
                && !CollectionUtils.isEmpty(newList))
            return batchInsert(newList);
        if (!CollectionUtils.isEmpty(oldList)
                && CollectionUtils.isEmpty(newList))
        {
            List<Object> oldIds = oldList.stream()
                    .map((elem) -> {
                        return ObjectUtils.getPrimaryKey(elem);
                    })
                    .collect(Collectors.toList());
            return deleteByPrimaryKeys(oldIds);
        }


        int size = newList.size();

        List<T> needInsertList = getNeedInsertList(newList);

        List<T> needUpdateList = getNeedUpdateList(newList,needInsertList);

        List<?> needDeleteIds = getNeedDeleteList(oldList,newList);

        batchInsert(needInsertList);
        batchUpdate(needUpdateList,isSelective);
        deleteByPrimaryKeys(needDeleteIds);

        return size;
    }

    protected List<?> getNeedDeleteList(List<T> oldList, List<T> newList)
    {
        List<Object> oldIds = oldList.stream()
                .map((elem) -> {
                    return ObjectUtils.getPrimaryKey(elem);
                })
                .collect(Collectors.toList());

        List<Object> newIds = newList.stream()
                .map((elem) -> {
                    return ObjectUtils.getPrimaryKey(elem);
                })
                .filter(elem->{
                    return elem != null;
                })
                .collect(Collectors.toList());

        oldIds.removeAll(newIds);

        return oldIds;
    }

    protected List<T> getNeedUpdateList(List<T> newList, List<T> needInsertList)
    {
        newList.removeAll(needInsertList);
        return newList;
    }

    protected List<T> getNeedInsertList(List<T> newList)
    {
        return newList.stream()
                .filter((elem)->{
                    return ObjectUtils.getPrimaryKey(elem) == null;
                })
                .collect(Collectors.toList());
    }
}
