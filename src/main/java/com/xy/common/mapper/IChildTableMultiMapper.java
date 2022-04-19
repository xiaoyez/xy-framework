package com.xy.common.mapper;


import com.xy.common.domain.IChildTableMultiObject;

/**
 * 需要做批量操作的子表mapper
 * @author xiaoye
 * @create 2021-09-23 14:27
 */
//@RegisterMapper
public interface IChildTableMultiMapper<T extends IChildTableMultiObject>
        extends Mapper<T> {

}
