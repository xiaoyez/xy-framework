package com.xy.common.utils.mybatis.definition.resultmap;

import com.xy.common.utils.xml.definition.XmlNodeDefinition;

/**
 * @author xiaoye
 * @create 2021-10-21 10:06
 */
public class ResultMapDefinition extends XmlNodeDefinition {
    {
        name = "resultMap";
    }

    public ResultMapDefinition setId(String id)
    {
        addAttribute("id",id);
        return this;
    }

    public ResultMapDefinition setType(Class type)
    {
        addAttribute("type",type.getName());
        return this;
    }

    public ResultMapDefinition setType(String typeName)
    {
        addAttribute("type",typeName);
        return this;
    }

    public ResultMapDefinition setAutoMapping(boolean autoMapping)
    {
        addAttribute("autoMapping",autoMapping?"true":"false");
        return this;
    }

    public ResultMapDefinition addAssociation(AssociationDefinition association)
    {
        addChild(association);
        return this;
    }

    public ResultMapDefinition addCollection(CollectionDefinition collection)
    {
        addChild(collection);
        return this;
    }


    public ResultMapDefinition addId(IdDefinition id)
    {
        addChild(id);
        return this;
    }

    public ResultMapDefinition addResult(ResultDefinition result)
    {
        addChild(result);
        return this;
    }

    private void sortChild()
    {
        children.sort((a,b)->{
            return Integer.valueOf(a.getPriority()).compareTo(b.getPriority());
        });
    }

    @Override
    public String toXmlString() {
        sortChild();
        return super.toXmlString();
    }
}
