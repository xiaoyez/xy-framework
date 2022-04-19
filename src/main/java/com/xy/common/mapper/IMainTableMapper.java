package com.xy.common.mapper;

import com.xy.common.domain.IMainTableObject;
import com.xy.common.mapper.provider.MainTableProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

/**
 * 主表mapper
 * @author xiaoye
 * @create 2021-09-23 11:40
 */
//@RegisterMapper
public interface IMainTableMapper <T extends IMainTableObject>
    extends Mapper<T>,
            IBatchDeleteMapper,
            IBatchSelectMapper<T>{

    @SelectProvider(
            type = MainTableProvider.class,
            method = "selectCompleteByPrimaryKey"
    )
    T selectCompleteByPrimaryKey(@Param("pk") Object primaryKey,
                                 @Param("className") String className);

    @SelectProvider(
            type = MainTableProvider.class,
            method = "selectCompleteOne"
    )
    T selectCompleteOne(T domain);

    @SelectProvider(
            type = MainTableProvider.class,
            method = "selectComplete"
    )
    List<T> selectComplete(T domain);

    @SelectProvider(
            type = MainTableProvider.class,
            method = "selectCompleteAll"
    )
    List<T> selectCompleteAll(@Param("className") String className);

}
