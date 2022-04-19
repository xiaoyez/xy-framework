package com.xy.common.mapper;

import com.xy.common.mapper.provider.SearchProvider;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

/**
 * @author xiaoye
 * @create 2021-10-13 11:07
 */
public interface ISearchAuthMapper <T> extends ISearchMapper<T>{

    @SelectProvider(
            type = SearchProvider.class,
            method = "searchAuth"
    )
    List<T> searchAuth(T domain);
}
