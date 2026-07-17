package com.spt.bas.server.rocketmq.tags;

public enum CommonTagsEnum {

    APPLY_MATCH("撮合表"),
    APPLY_MATCH_SIGN("撮合表-单个"),
    APPLY_MATCH_DETAIL("撮合表详情表"),
    APPLY_MATCH_DETAIL_SIGN("撮合表详情表-单个"),
    PM_APPROVE("审批表"),
    PM_APPROVE_SIGN("审批表-单个"),
    ;


    private final String description;

    CommonTagsEnum(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }


}
