package com.xy.common.mapper;

import com.xy.common.mapper.provider.BatchMapperProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.UpdateProvider;

import java.util.List;

/**
 * @author xiaoye
 * @create 2021-09-24 11:11
 */
//@RegisterMapper
public interface IBatchMapper<T> extends IBatchDeleteMapper, IBatchSelectMapper<T> {

    @InsertProvider(
            type = BatchMapperProvider.class,
            method = "batchInsert"
    )
    int batchInsert(@Param("list") List<T> list);

    @UpdateProvider(
            type = BatchMapperProvider.class,
            method = "batchUpdateSelective"
    )
    int batchUpdateSelective(@Param("list") List<T> list);

    @UpdateProvider(
            type = BatchMapperProvider.class,
            method = "batchUpdate"
    )
    @Options(useGeneratedKeys = false)
    int batchUpdate(@Param("list") List<T> list);
}
