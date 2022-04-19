package com.xy.common.mapper;

import com.xy.common.mapper.provider.MapperProvider;
import org.apache.ibatis.annotations.UpdateProvider;

/**
 * @author xiaoye
 * @create 2021-11-04 11:38
 */
public interface Mapper<T> extends tk.mybatis.mapper.common.Mapper<T>,
    IBatchMapper<T>{

    @UpdateProvider(
            type = MapperProvider.class,
            method = "updateSelective"
    )
    int updateSelective(T domain);
}
