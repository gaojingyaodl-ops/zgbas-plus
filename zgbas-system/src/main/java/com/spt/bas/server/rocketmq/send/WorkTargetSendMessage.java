package com.spt.bas.server.rocketmq.send;

import cn.hutool.json.JSONUtil;
import com.spt.bas.client.entity.WorkTarget;
import com.spt.bas.server.rocketmq.RocketmqCustomProperties;
import com.spt.bas.server.rocketmq.tags.WorkTargetTagsEnum;
import com.spt.bas.server.rocketmq.util.RocketmqSendCallbackBuilder;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2023/3/6 15:35
 */
@Component
public class WorkTargetSendMessage {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    @Autowired
    private RocketmqCustomProperties rocketmqCustomProperties;

    protected Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * 新增或更新目标
     *
     * @param workTarget
     */
    public void addWorkTarget(WorkTarget workTarget) {
        String messageBody = JSONUtil.toJsonStr(workTarget);
        String topic = rocketmqCustomProperties.getWorkTargetTopic() + ":" + WorkTargetTagsEnum.ADD.name();
        log.info("新增-workTarget：{}", topic);
        rocketMQTemplate.asyncSend(topic, messageBody, RocketmqSendCallbackBuilder.commonCallback());
    }

    public void updateWorkTarget(WorkTarget workTarget) {
        String messageBody = JSONUtil.toJsonStr(workTarget);
        String topic = rocketmqCustomProperties.getWorkTargetTopic() + ":" + WorkTargetTagsEnum.UPDATE.name();
        log.info("更新-workTarget：{}", topic);
        rocketMQTemplate.asyncSend(topic, messageBody, RocketmqSendCallbackBuilder.commonCallback());
    }

    /**
     * 删除
     *
     * @param id
     */
    public void deleteWorkTarget(Long id) {
        String topic = rocketmqCustomProperties.getWorkTargetTopic() + ":" + WorkTargetTagsEnum.DELETE.name();
        log.info("删除-workTarget：{}", topic);
        rocketMQTemplate.asyncSend(topic, id, RocketmqSendCallbackBuilder.commonCallback());
    }


}
