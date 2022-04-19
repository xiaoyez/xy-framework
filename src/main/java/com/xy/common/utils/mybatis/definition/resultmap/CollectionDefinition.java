package com.xy.common.utils.mybatis.definition.resultmap;

import com.xy.common.utils.xml.definition.XmlNodeDefinition;

/**
 * @author xiaoye
 * @create 2021-10-21 10:09
 */
public class CollectionDefinition extends XmlNodeDefinition {

    {
        name = "collection";
        priority = 4;
    }

    public CollectionDefinition setColumn(String column)
    {
        addAttribute("column",column);
        return this;
    }

    public CollectionDefinition setProperty(String property)
    {
        addAttribute("property",property);
        return this;
    }

    public CollectionDefinition setOfType(Class ofType)
    {
        addAttribute("ofType",ofType.getName());
        return this;
    }

    public CollectionDefinition setOfType(String ofType)
    {
        addAttribute("ofType",ofType);
        return this;
    }

    public CollectionDefinition setAutoMapping(boolean autoMapping)
    {
        addAttribute("autoMapping",autoMapping?"true":"false");
        return this;
    }

    public CollectionDefinition addId(IdDefinition id)
    {
        addChild(id);
        return this;
    }

    public CollectionDefinition addResult(ResultDefinition result)
    {
        addChild(result);
        return this;
    }
}
