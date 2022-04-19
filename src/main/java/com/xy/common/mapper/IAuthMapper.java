package com.xy.common.mapper;

import com.xy.common.mapper.provider.AuthProvider;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

/**
 * @author xiaoye
 * @create 2021-10-09 16:11
 */
public interface IAuthMapper<T> {

    @SelectProvider(
            type = AuthProvider.class,
            method = "selectAuth"
    )
    List<T> selectAuth(T domain);


    @SelectProvider(
            type = AuthProvider.class,
            method = "selectCompleteAuth"
    )
    List<T> selectCompleteAuth(T domain);
}
