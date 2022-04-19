package com.xy.common.utils.mybatis.definition.resultmap;

import com.xy.common.utils.xml.definition.XmlNodeDefinition;

/**
 * @author xiaoye
 * @create 2021-10-21 10:07
 */
public class ResultDefinition extends XmlNodeDefinition {
    {
        name = "result";
    }

    public ResultDefinition setColumn(String column)
    {
        addAttribute("column",column);
        return this;
    }

    public ResultDefinition setProperty(String property)
    {
        addAttribute("property",property);
        return this;
    }
}
