package com.xy.common.utils.mybatis.definition.resultmap;

import com.xy.common.utils.xml.definition.XmlNodeDefinition;

/**
 * @author xiaoye
 * @create 2021-10-21 10:08
 */
public class AssociationDefinition extends XmlNodeDefinition {
    {
        name = "association";
        priority = 3;
    }

    public AssociationDefinition setColumn(String column)
    {
        addAttribute("column",column);
        return this;
    }

    public AssociationDefinition setProperty(String property)
    {
        addAttribute("property",property);
        return this;
    }

    public AssociationDefinition setAutoMapping(boolean autoMapping)
    {
        addAttribute("autoMapping",autoMapping?"true":"false");
        return this;
    }

    public AssociationDefinition setJavaType(Class javaType)
    {
        addAttribute("javaType",javaType.getName());
        return this;
    }

    public AssociationDefinition setJavaType(String javaType)
    {
        addAttribute("javaType",javaType);
        return this;
    }

    public AssociationDefinition addId(IdDefinition id)
    {
        addChild(id);
        return this;
    }

    public AssociationDefinition addResult(ResultDefinition result)
    {
        addChild(result);
        return this;
    }
}
