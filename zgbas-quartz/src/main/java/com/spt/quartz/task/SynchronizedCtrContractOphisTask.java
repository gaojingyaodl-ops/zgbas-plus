package com.spt.quartz.task;

import cn.hutool.json.JSONUtil;
import com.spt.bas.client.entity.CtrContractOphis;
import com.spt.bas.server.dao.CtrContractOphisDao;
import com.spt.bas.server.rocketmq.RocketmqCustomProperties;
import com.spt.bas.server.rocketmq.tags.ContractTagsEnum;
import com.spt.bas.server.rocketmq.util.RocketmqSendCallbackBuilder;
import com.spt.bas.server.rocketmq.util.ThreadPoolExecutorEngine;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * Phase 6 (06-03) — ported from {@code com.spt.bas.server.rocketmq.task.SynchronizedCtrContractOphisTask}.
 * Bean name {@code "synchronizedCtrContractOphisTask"} aligns with {@code sys_job.invoke_target}
 * short name {@code synchronizedCtrContractOphisTask.synchronizedAllCtrContractOphis}. Per D-P6-11,
 * this handler remains as a sys_job-scheduled entry point; MQApi HTTP endpoint will be refactored
 * in 06-04 to call the underlying service directly (two coexisting entry points).
 *
 * @author 杨英承
 * @version 1.0.0
 * @date 2023/3/7 16:09
 */
@Component("synchronizedCtrContractOphisTask")
public class SynchronizedCtrContractOphisTask {
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    @Autowired
    private RocketmqCustomProperties rocketmqCustomProperties;
    @Autowired
    private CtrContractOphisDao ctrContractOphisDao;

    protected Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * 查询数据
     */
    public static final int PAGE_COUNT = 100;


    public void synchronizedAllCtrContractOphis() {
        log.info("同步数据中台合同历史数据定时任务开始======>");
        ThreadPoolExecutor engine = ThreadPoolExecutorEngine.getInstance();
        Integer totalCount = ctrContractOphisDao.selectAllCount();
        // 计算要查多少次
        int pages = totalCount % PAGE_COUNT == 0 ? totalCount / PAGE_COUNT : totalCount / PAGE_COUNT + 1;
        // 加入任务列表
        for (int pageIndex = 0; pageIndex < pages; pageIndex++) {
            engine.execute(new SynchronizedCtrContractOphisTask.ThreadQuery(pageIndex, PAGE_COUNT));
        }
        log.info("同步数据中台 t_ctr_contract_ophis 数据定时任务 执行成功!");
    }

    private void getCtrContractOphis(int pageIndex, int perPageCount) {
        Pageable page = PageRequest.of(pageIndex, perPageCount);
        Page<CtrContractOphis> pageData = ctrContractOphisDao.findAll(page);
        List<CtrContractOphis> ctrContractOphisList = pageData.get().collect(Collectors.toList());
        log.info("查询了数据数量为---{}", ctrContractOphisList.size());
        String topic = rocketmqCustomProperties.getContractHistoryTopic() + ":" + ContractTagsEnum.OPHIS.name();
        log.info("MQ主题为---{}", topic);
        for (CtrContractOphis ctrContractOphis : ctrContractOphisList) {
            String messageBody = JSONUtil.toJsonStr(ctrContractOphis);
            rocketMQTemplate.asyncSend(topic, messageBody, RocketmqSendCallbackBuilder.commonCallback());
        }
    }


    class ThreadQuery implements Runnable {

        private int pageIndex;
        private int perPageCount;

        public ThreadQuery(int pageIndex, int perPageCount) {
            this.pageIndex = pageIndex;
            this.perPageCount = perPageCount;
        }

        @Override
        public void run() {
            getCtrContractOphis(this.pageIndex, this.perPageCount);
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
