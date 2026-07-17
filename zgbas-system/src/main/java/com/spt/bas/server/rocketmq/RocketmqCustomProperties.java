package com.spt.bas.server.rocketmq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ConfigurationProperties(prefix = "app.rocketmq")
public class RocketmqCustomProperties {

    private String demoStringTopic;

    private String demoStringGroup;

    private String demoOrderTopic;

    private String demoOrderGroup;

    /**
     * 合同主体
     */
    private String contractTopic;

    /**
     * 合同产品详情主题
     */
    private String ctrProduct;

    /**
     * 公共主题
     */
    private String commonTopic;

    private String contractGroup;

    private String workTargetGroup;

    /**
     * 任务目标主题
     */
    private String workTargetTopic;

    private String contractHistoryTopic;

    /**
     * 客户信息主题
     */
    private String companyTopic;

}
