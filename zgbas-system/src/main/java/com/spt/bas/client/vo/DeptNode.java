package com.spt.bas.client.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * 企业组织结构节点
 */
public class DeptNode {
    private Long id;

    private String name;

    private String type;

    private Long parentId;

    private List<DeptNode> children = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public List<DeptNode> getChildren() {
        return children;
    }

    public void setChildren(List<DeptNode> children) {
        this.children = children;
    }
}
