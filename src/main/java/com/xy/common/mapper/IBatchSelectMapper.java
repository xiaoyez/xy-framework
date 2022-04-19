package com.xy.common.mapper;

import com.xy.common.mapper.provider.BatchMapperProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

/**
 * @author xiaoye
 * @create 2021-10-13 15:32
 */
public interface IBatchSelectMapper<T> {

    @SelectProvider(
            type = BatchMapperProvider.class,
            method = "selectByPrimaryKeys"
    )
    List<T> selectByPrimaryKeys(@Param("list") List<?> ids, @Param("className") String className);
}
