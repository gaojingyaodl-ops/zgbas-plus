package com.spt.quartz.task;

import cn.hutool.json.JSONUtil;
import com.spt.bas.client.entity.ApplyMatch;
import com.spt.bas.server.dao.ApplyMatchDao;
import com.spt.bas.server.rocketmq.RocketmqCustomProperties;
import com.spt.bas.server.rocketmq.tags.CommonTagsEnum;
import com.spt.bas.server.rocketmq.util.RocketmqSendCallbackBuilder;
import com.spt.bas.server.rocketmq.util.ThreadPoolExecutorEngine;
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
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * 同步老审批和数据中台合同表数据定时任务
 *
 * <p>Phase 6 (06-03) — ported from {@code com.spt.bas.server.rocketmq.task.SynchronizedApplyMatchTask}.
 * Bean name {@code "synchronizedApplyMatchTask"} aligns with {@code sys_job.invoke_target}
 * short name {@code synchronizedApplyMatchTask.synchronizedAllApplyMatch}. Per D-P6-11,
 * this handler remains as a sys_job-scheduled entry point; MQApi HTTP endpoint will be refactored
 * in 06-04 to call the underlying service directly (two coexisting entry points).
 *
 * @author 杨英承
 * @version 1.0.0
 * @date 2023/2/27 11:42
 */
@Component("synchronizedApplyMatchTask")
public class SynchronizedApplyMatchTask {


    @Autowired
    private ApplyMatchDao applyMatchDao;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    @Autowired
    private RocketmqCustomProperties rocketmqCustomProperties;

    protected Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * 查询数据
     */
    public static final int PAGE_COUNT = 100;

    /**
     * 全量同步数据中台的合同数据
     */
    public void synchronizedAllApplyMatch() {
        log.info("同步数据中台合同撮合表定时任务开始======>");
        ThreadPoolExecutor engine = ThreadPoolExecutorEngine.getInstance();
        Integer totalCount = applyMatchDao.selectAllCount();
        // 计算要查多少次
        int pages = totalCount % PAGE_COUNT == 0 ? totalCount / PAGE_COUNT : totalCount / PAGE_COUNT + 1;
        // 加入任务列表
        for (int pageIndex = 0; pageIndex < pages; pageIndex++) {
            engine.execute(new ThreadQuery(pageIndex, PAGE_COUNT));
        }
        //ThreadPoolExecutorEngine.shutdown();
        log.info("同步数据中台 t_apply_match 数据定时任务 执行成功!");
    }

    private void getApplyMatch(int pageIndex, int perPageCount) {
        Pageable page = PageRequest.of(pageIndex, perPageCount);
        Page<ApplyMatch> pageData = applyMatchDao.findAll(page);
        List<ApplyMatch> applyMatches = pageData.get().collect(Collectors.toList());
        log.info("查询了数据数量为---{}", applyMatches.size());
        String topic = rocketmqCustomProperties.getCommonTopic() + ":" + CommonTagsEnum.APPLY_MATCH.name();
        log.info("MQ主题为---{}", topic);
        for (ApplyMatch applyMatch : applyMatches) {
            String messageBody = JSONUtil.toJsonStr(applyMatch);
            // 发送使用带key的消息，使用RocketMQ自带的 Message，设置key的方法行不通！！！
            Message<String> message = MessageBuilder.withPayload(messageBody).build();
            rocketMQTemplate.asyncSend(topic, message, RocketmqSendCallbackBuilder.commonCallback());
        }
    }

    /**
     * 内部类，可返回值的线程类
     */
    class ThreadQuery implements Runnable {

        private int pageIndex;
        private int perPageCount;

        public ThreadQuery(int pageIndex, int perPageCount) {
            this.pageIndex = pageIndex;
            this.perPageCount = perPageCount;
        }

        @Override
        public void run() {
            getApplyMatch(this.pageIndex, this.perPageCount);
        }

        public int getPageIndex() {
            return pageIndex;
        }

        public void setPageIndex(int pageIndex) {
            this.pageIndex = pageIndex;
        }

        public int getPerPageCount() {
            return perPageCount;
        }

        public void setPerPageCount(int perPageCount) {
            this.perPageCount = perPageCount;
        }
    }
}
