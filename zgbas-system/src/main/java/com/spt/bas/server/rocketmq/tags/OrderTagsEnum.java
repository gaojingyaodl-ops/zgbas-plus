package com.spt.bas.server.rocketmq.tags;

public enum OrderTagsEnum {

    ORDER_CREATE("订单创建"),
    STATUS_CHANGE("订单状态改变");


    private final String description;

    OrderTagsEnum(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }


}
