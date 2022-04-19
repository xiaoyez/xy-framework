package com.xy.common.domain;


import com.xy.common.service.IBaseService;
import com.xy.common.service.IChildTableMultiService;
import lombok.Data;
import org.springframework.util.CollectionUtils;
import com.xy.common.mapper.Mapper;

import java.util.List;

/**
 * 子表对象
 * @author xiaoye
 * @create 2021-09-23 16:48
 */
@Data
public class ChildTableCompleteObject {

    private List<ChildTableSingleCompleteObject<
                IChildTableSingleObject,
                IBaseService<
                        IChildTableSingleObject,
                        ? extends Mapper<IChildTableSingleObject>
                        >
                >
            > singleCompleteObjects;

    private List<ChildTableMultiCompleteObject<
                IChildTableMultiObject,
                IChildTableMultiService<
                        IChildTableMultiObject,
                        ?
                        >
                >
            > multiCompleteObjects;

    public void setFK(Object fk, Class mainTableClass) {
        if (!CollectionUtils.isEmpty(singleCompleteObjects))
        {
            for (ChildTableSingleCompleteObject singleCompleteObject : singleCompleteObjects)
            {
                if (singleCompleteObject != null)
                {
                    IChildTableSingleObject object = singleCompleteObject.getObject();
                    if (object != null)
                    {
                        int fkCount = object.getFkCount();
                        if (fkCount == 1)
                            object.setFK(fk);
                        else
                            object.setFK(fk,mainTableClass);
                    }
                }
            }
        }

        if (!CollectionUtils.isEmpty(multiCompleteObjects))
        {
            for (ChildTableMultiCompleteObject multiCompleteObject : multiCompleteObjects)
            {
                if (multiCompleteObject != null)
                {
                    List<? extends IChildTableMultiObject> objects = multiCompleteObject.getObjects();
                    if (!CollectionUtils.isEmpty(objects))
                    {
                        for (IChildTableMultiObject object : objects)
                        {
                            if (object != null)
                            {
                                int fkCount = object.getFkCount();
                                if (fkCount == 1)
                                    object.setFK(fk);
                                else
                                    object.setFK(fk,mainTableClass);
                            }
                        }
                    }
                }
            }
        }
    }


    @Data
    public static class ChildTableSingleCompleteObject
            <T extends IChildTableSingleObject,
             S extends IBaseService<T, ? extends Mapper<T>>>{
        T object;
        S service;
    }

    @Data
    public static class ChildTableMultiCompleteObject
            <T extends IChildTableMultiObject,
             S extends IChildTableMultiService<T,?>>{
        List<T> objects;
        S service;
    }
}
