package com.spt.quartz.task;

import cn.hutool.json.JSONUtil;
import com.spt.bas.client.entity.WorkTarget;
import com.spt.bas.server.dao.WorkTargetDao;
import com.spt.bas.server.rocketmq.RocketmqCustomProperties;
import com.spt.bas.server.rocketmq.tags.WorkTargetTagsEnum;
import com.spt.bas.server.rocketmq.util.RocketmqSendCallbackBuilder;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Phase 6 (06-03) — ported from {@code com.spt.bas.server.rocketmq.task.SynchronizedWorkTargetTask}.
 * Bean name {@code "synchronizedWorkTargetTask"} aligns with {@code sys_job.invoke_target}
 * short names {@code synchronizedWorkTargetTask.synchronizedAllWorkTarget} and
 * {@code synchronizedWorkTargetTask.testSendMessage}. Per D-P6-11, this handler remains as a
 * sys_job-scheduled entry point; MQApi HTTP endpoints will be refactored in 06-04 to call the
 * underlying service directly (two coexisting entry points).
 *
 * @author 杨英承
 * @version 1.0.0
 * @date 2023/3/7 14:24
 */
@Component("synchronizedWorkTargetTask")
public class SynchronizedWorkTargetTask {
    @Autowired
    private WorkTargetDao workTargetDao;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    @Autowired
    private RocketmqCustomProperties rocketmqCustomProperties;

    /**
     * 查询数据
     */
    public static final int PAGE_COUNT = 100;

    protected Logger log = LoggerFactory.getLogger(this.getClass());


    /**
     * 全量同步WorkTarget目标
     */
    public void synchronizedAllWorkTarget() {
        String topic = rocketmqCustomProperties.getWorkTargetTopic() + ":" + WorkTargetTagsEnum.ALL.name();
        Integer totalCount = workTargetDao.selectAllCount();
        log.info("查询数据为{}", totalCount);
        int pages = totalCount % PAGE_COUNT == 0 ? totalCount / PAGE_COUNT : totalCount / PAGE_COUNT + 1;
        log.info("MQ主题为---{}", topic);
        for (int pageIndex = 0; pageIndex < pages; pageIndex++) {
            Pageable page = PageRequest.of(pageIndex, PAGE_COUNT);
            Page<WorkTarget> pageData = workTargetDao.findAll(page);
            List<WorkTarget> workTargetList = pageData.get().collect(Collectors.toList());
            sendList(workTargetList, topic);
        }
        log.info("synchronizedAllWorkTarget同步结束！");
        log.info("同步数据中台 work_target 数据定时任务 执行成功!");
    }

    /**
     * 发送消息
     *
     * @param workTargetList 数据
     */
    private void sendList(List<WorkTarget> workTargetList, String topic) {
        for (WorkTarget workTarget : workTargetList) {
            String messageBody = JSONUtil.toJsonStr(workTarget);
            rocketMQTemplate.asyncSend(topic, messageBody, RocketmqSendCallbackBuilder.commonCallback());
        }
    }

    public void testSendMessage(){
        log.info("测试同步消息发送！");
        //String topic = rocketmqCustomProperties.getWorkTargetTopic() + ":" + WorkTargetTagsEnum.ALL.name();
        Message<String> message = MessageBuilder.withPayload("这个是测试消息").build();
        rocketMQTemplate.send("yyc-data",message);
        log.info("测试同步消息发送完成！");
    }
}
