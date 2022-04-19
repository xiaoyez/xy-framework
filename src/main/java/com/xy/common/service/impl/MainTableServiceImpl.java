package com.xy.common.service.impl;


import com.xy.common.domain.ChildTableCompleteObject;
import com.xy.common.domain.IChildTableMultiObject;
import com.xy.common.domain.IChildTableSingleObject;
import com.xy.common.domain.IMainTableObject;
import com.xy.common.mapper.IMainTableMapper;
import com.xy.common.service.IBaseService;
import com.xy.common.service.IChildTableMultiService;
import com.xy.common.service.IMainTableService;
import com.xy.common.utils.ObjectUtils;
import com.xy.common.utils.mybatis.MybatisUtils;
import lombok.var;
import org.springframework.util.CollectionUtils;
import com.xy.common.mapper.Mapper;

import java.util.List;

/**
 * 主表Service
 * @author xiaoye
 * @create 2021-09-23 15:07
 */
public abstract class MainTableServiceImpl<T extends IMainTableObject, M extends Mapper<T> & IMainTableMapper<T>>
        extends BaseServiceImpl<T,M>
        implements IMainTableService<T,M> {

    @Override
    public T getCompleteByPrimaryKey(Object primaryKey) {
        return mapper.selectCompleteByPrimaryKey(primaryKey,getDomainClassName());
    }

    @Override
    public T getCompleteOne(T domain) {
        return mapper.selectCompleteOne(domain);
    }

    @Override
    public List<T> getsComplete(T domain) {
        return mapper.selectComplete(domain);
    }

    @Override
    public List<T> getCompleteAll() {
        return mapper.selectCompleteAll(getDomainClassName());
    }

    @Override
    public int insertComplete(T domain) {
        int result = insert(domain);
        insertChildTable(domain);
        return result;
    }

    @Override
    public int updateComplete(T domain) {
        return updateComplete(domain,true);
    }

    @Override
    public int updateComplete(T domain, boolean isSelective) {
        int result;
        if (MybatisUtils.getDbType().equals(MybatisUtils.DbType.SQLSERVER))
        {
            Object pk = ObjectUtils.getPrimaryKey(domain);
            result = update(domain,isSelective);
            ObjectUtils.setPrimaryKey(domain,pk);
        }
        else
        {
            result = update(domain,isSelective);
        }
        updateChildTable(domain,isSelective);
        return result;

    }

    protected void insertChildTable(T domain)
    {
        ChildTableCompleteObject childTableCompleteObject = domain.childTableCompleteObject();
        childTableCompleteObject.setFK(domain.getFK(),domain.getClass());
        var singleCompleteObjects = childTableCompleteObject.getSingleCompleteObjects();
        if (!CollectionUtils.isEmpty(singleCompleteObjects))
        {
            for (ChildTableCompleteObject.ChildTableSingleCompleteObject singleCompleteObject : singleCompleteObjects) {
                IBaseService service = singleCompleteObject.getService();
                IChildTableSingleObject object = singleCompleteObject.getObject();
                service.insert(object);
            }
        }

        var multiCompleteObjects = childTableCompleteObject.getMultiCompleteObjects();
        if (!CollectionUtils.isEmpty(multiCompleteObjects))
        {
            for (ChildTableCompleteObject.ChildTableMultiCompleteObject multiCompleteObject : multiCompleteObjects) {
                IChildTableMultiService service = multiCompleteObject.getService();
                List<?> objects = multiCompleteObject.getObjects();
                service.batchInsert(objects);
            }
        }
    }

    protected void updateChildTable(T domain,boolean isSelective)
    {
        ChildTableCompleteObject childTableCompleteObject = domain.childTableCompleteObject();
        childTableCompleteObject.setFK(domain.getFK(),domain.getClass());

        T oldDomain = getCompleteByPrimaryKey(ObjectUtils.getPrimaryKey(domain));
        ChildTableCompleteObject oldChildTableCompleteObject = oldDomain.childTableCompleteObject();

        updateChildSingleObject(oldChildTableCompleteObject.getSingleCompleteObjects(),
                childTableCompleteObject.getSingleCompleteObjects(),isSelective);

        updateChildMultiObject(oldChildTableCompleteObject.getMultiCompleteObjects(),
                childTableCompleteObject.getMultiCompleteObjects(),isSelective);
    }

    protected void updateChildMultiObject(
            List<ChildTableCompleteObject.ChildTableMultiCompleteObject<
                    IChildTableMultiObject,
                    IChildTableMultiService<
                            IChildTableMultiObject,
                            ?
                            >
                    >
                    > oldMultiCompleteObjects,
            List<ChildTableCompleteObject.ChildTableMultiCompleteObject<
                    IChildTableMultiObject,
                    IChildTableMultiService<
                            IChildTableMultiObject,
                            ?
                            >
                    >
                    > newMultiCompleteObjects,
            boolean isSelective)
    {
        int size = 0;
        if (!CollectionUtils.isEmpty(oldMultiCompleteObjects))
        {
            size = oldMultiCompleteObjects.size();
        }
        if (!CollectionUtils.isEmpty(newMultiCompleteObjects))
        {
            size = Math.max(size,newMultiCompleteObjects.size());
        }
        if (size == 0)
            return;
        for (int i = 0; i < size; i++) {
            updateChildMultiObject(oldMultiCompleteObjects.get(i),
                    newMultiCompleteObjects.get(i),
                    isSelective);
        }
    }

    protected void updateChildMultiObject(ChildTableCompleteObject.ChildTableMultiCompleteObject oldChildTableMultiCompleteObject, ChildTableCompleteObject.ChildTableMultiCompleteObject newChildTableMultiCompleteObject,boolean isSelective)
    {
        List<?> oldObjects = oldChildTableMultiCompleteObject.getObjects();
        List<?> newObjects = newChildTableMultiCompleteObject.getObjects();

        IChildTableMultiService service = newChildTableMultiCompleteObject.getService();
        service.batchUpdate(oldObjects,newObjects,isSelective);
    }

    protected void updateChildSingleObject(
            List<ChildTableCompleteObject.ChildTableSingleCompleteObject<
                    IChildTableSingleObject,
                    IBaseService<
                            IChildTableSingleObject,
                            ? extends Mapper<IChildTableSingleObject>
                            >
                    >
                    > oldSingleCompleteObjects,
            List<ChildTableCompleteObject.ChildTableSingleCompleteObject<
                    IChildTableSingleObject,
                    IBaseService<
                            IChildTableSingleObject,
                            ? extends Mapper<IChildTableSingleObject>
                            >
                    >
                    > newSingleCompleteObjects,
            boolean isSelective)
    {
        int size = 0;
        if (!CollectionUtils.isEmpty(oldSingleCompleteObjects))
        {
            size = oldSingleCompleteObjects.size();
        }
        if (!CollectionUtils.isEmpty(newSingleCompleteObjects))
        {
            size = Math.max(size,newSingleCompleteObjects.size());
        }
        if (size == 0)
            return;
        for (int i = 0; i < size; i++) {
            updateChildSingleObject(oldSingleCompleteObjects.get(i),
                    newSingleCompleteObjects.get(i),
                    isSelective);
        }
    }

    protected void updateChildSingleObject(ChildTableCompleteObject.ChildTableSingleCompleteObject oldChildTableSingleCompleteObject, ChildTableCompleteObject.ChildTableSingleCompleteObject newChildTableSingleCompleteObject, boolean isSelective) {
        IChildTableSingleObject oldObject = oldChildTableSingleCompleteObject.getObject();
        IChildTableSingleObject newObject = newChildTableSingleCompleteObject.getObject();

        IBaseService service = newChildTableSingleCompleteObject.getService();
        if (oldObject != null && newObject == null)
        {
            service.deleteByPrimaryKey(ObjectUtils.getPrimaryKey(oldObject));
        }
        if (oldObject == null && newObject != null)
        {
            service.insert(newObject);
        }
        if (oldObject != null && newObject != null)
        {
            service.update(newObject,isSelective);
        }
    }
}
