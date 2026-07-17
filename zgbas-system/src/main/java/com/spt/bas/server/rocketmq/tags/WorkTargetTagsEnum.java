package com.spt.bas.server.rocketmq.tags;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2023/3/6 15:48
 */

public enum WorkTargetTagsEnum {

    ADD("新增指标"),
    UPDATE("更新指标"),
    DELETE("删除指标"),
    ALL("全量同步");

    private final String description;

    WorkTargetTagsEnum(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }
}
