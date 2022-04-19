package com.xy.common.mapper.provider;

import cn.hutool.core.util.ReflectUtil;
import lombok.var;

/**
 * @author xiaoye
 * @create 2021-10-09 16:14
 */
public class AuthProvider extends AbstractProvider{

    private MainTableProvider mainTableProvider = new MainTableProvider();


    public String selectAuth(Object domain)
    {
        var builder = new StringBuilder();
        String pkName = getPrimaryKeyName(domain.getClass());
        String authQuerySql = ReflectUtil.getFieldValue(domain,"authQuerySql").toString();
        builder.append(mainTableProvider.selectComplete(domain))
                .append(" and ")
                .append(pkName)
                .append(" in ( ")
                .append(authQuerySql)
                .append(" )");
        return builder.toString();
    }


    public String selectCompleteAuth(Object domain)
    {
        var builder = new StringBuilder();
        String pkName = getPrimaryKeyName(domain.getClass());
        builder.append(mainTableProvider.selectComplete(domain))
                .append(" and ")
                .append(pkName)
                .append(" in ( ${authQuerySql} )");
        return builder.toString();
    }
}
