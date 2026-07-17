package com.spt.bas.server.rocketmq.tags;

public enum ContractTagsEnum {

    SIGN("签约"),
    STATUS_CHANGE("状态改变"),
    ALL_CONTRACT("同步全量数据"),
    OPHIS("合同历史"),
    OPHIS_SIGN("合同历史-单个"),
    PRODUCT("物料详情表"),
    PRODUCT_SIGN("物料详情表-单个"),
    ;


    private final String description;

    ContractTagsEnum(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }


}
