package com.xy.common.mapper;

import com.xy.common.mapper.provider.BatchMapperProvider;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author xiaoye
 * @create 2021-09-29 17:17
 */
//@RegisterMapper
public interface IBatchDeleteMapper {

    @DeleteProvider(
            type = BatchMapperProvider.class,
            method = "deleteByPrimaryKeys"
    )
    int deleteByPrimaryKeys(@Param("list") List<?> primaryKeys, @Param("className") String className);
}
