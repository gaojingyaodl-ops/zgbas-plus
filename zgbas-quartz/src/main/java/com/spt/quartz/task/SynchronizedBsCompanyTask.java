package com.spt.quartz.task;

import cn.hutool.json.JSONUtil;
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.server.dao.BsCompanyDao;
import com.spt.bas.server.rocketmq.RocketmqCustomProperties;
import com.spt.bas.server.rocketmq.tags.BsCompanyTagsEnum;
import com.spt.bas.server.rocketmq.util.RocketmqSendCallbackBuilder;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 全量同步 t_bs_company 数据
 *
 * <p>Phase 6 (06-03) — ported from {@code com.spt.bas.server.rocketmq.task.SynchronizedBsCompanyTask}.
 * Bean name {@code "synchronizedBsCompanyTask"} aligns with {@code sys_job.invoke_target}
 * short name {@code synchronizedBsCompanyTask.synchronizedAllBsCompany}. Per D-P6-11,
 * this handler remains as a sys_job-scheduled entry point; MQApi HTTP endpoint will be refactored
 * in 06-04 to call the underlying service directly (two coexisting entry points).
 *
 * @author 杨英承
 * @version 1.0.0
 * @date 2023/3/7 14:24
 */
@Component("synchronizedBsCompanyTask")
public class SynchronizedBsCompanyTask {
    @Autowired
    private BsCompanyDao bsCompanyDao;
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
    public void synchronizedAllBsCompany() {
        String topic = rocketmqCustomProperties.getCompanyTopic() + ":" + BsCompanyTagsEnum.ALL.name();
        Integer totalCount = bsCompanyDao.selectAllCount();
        int pages = totalCount % PAGE_COUNT == 0 ? totalCount / PAGE_COUNT : totalCount / PAGE_COUNT + 1;
        log.info("MQ主题为---{}", topic);
        for (int pageIndex = 0; pageIndex < pages; pageIndex++) {
            Pageable page = PageRequest.of(pageIndex, PAGE_COUNT);
            Page<BsCompany> pageData = bsCompanyDao.findAll(page);
            List<BsCompany> companyList = pageData.get().collect(Collectors.toList());
            sendList(companyList, topic);
        }
        log.info("同步数据中台 t_bs_company 数据定时任务 执行成功!");
    }

    /**
     * 发送消息
     *
     * @param companyList 数据
     */
    private void sendList(List<BsCompany> companyList, String topic) {
        for (BsCompany company : companyList) {
            String messageBody = JSONUtil.toJsonStr(company);
            rocketMQTemplate.asyncSend(topic, messageBody, RocketmqSendCallbackBuilder.commonCallback());
        }
    }
}
