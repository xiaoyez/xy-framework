package com.xy.common.utils.xml.definition;

import lombok.Data;

import java.util.*;

/**
 * @author xiaoye
 * @create 2021-10-21 9:59
 */
@Data
public abstract class XmlNodeDefinition {

    protected String name;

    protected Map<String,String> attributes;

    protected List<XmlNodeDefinition> children;

    // 优先级
    protected int priority;

    protected void addChild(XmlNodeDefinition child)
    {
        if (children == null)
            children = new ArrayList<>();
        children.add(child);
    }

    protected void addAttribute(String name,String value)
    {
        if (attributes == null)
            attributes = new LinkedHashMap<>();
        attributes.put(name,value);
    }

    public String toXmlString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append(open());
        if (children != null)
        {
            builder.append("\n");
            for (XmlNodeDefinition child : children) {
                builder.append(child.toXmlString());
            }
        }
        builder.append(close());
        return builder.toString();
    }

    protected String open()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("<")
                .append(name)
                .append(" ");
        if (attributes != null)
        {
            Set<Map.Entry<String, String>> entries = attributes.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                builder.append(entry.getKey())
                        .append("=\"")
                        .append(entry.getValue())
                        .append("\" ");
            }
        }
        builder.append(">");
        return builder.toString();
    }

    protected String close()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("</")
                .append(name)
                .append(">\n");
        return builder.toString();
    }
}
