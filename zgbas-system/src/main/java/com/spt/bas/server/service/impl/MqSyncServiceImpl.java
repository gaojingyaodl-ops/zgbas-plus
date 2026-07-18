package com.spt.bas.server.service.impl;

import cn.hutool.json.JSONUtil;
import com.spt.bas.client.entity.ApplyMatch;
import com.spt.bas.client.entity.ApplyMatchDetail;
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.entity.CtrContractOphis;
import com.spt.bas.client.entity.CtrProduct;
import com.spt.bas.client.entity.WorkTarget;
import com.spt.bas.server.dao.ApplyMatchDetailDao;
import com.spt.bas.server.dao.ApplyMatchDao;
import com.spt.bas.server.dao.BsCompanyDao;
import com.spt.bas.server.dao.CtrContractDao;
import com.spt.bas.server.dao.CtrContractOphisDao;
import com.spt.bas.server.dao.CtrProductDao;
import com.spt.bas.server.dao.WorkTargetDao;
import com.spt.bas.server.rocketmq.RocketmqCustomProperties;
import com.spt.bas.server.rocketmq.tags.BsCompanyTagsEnum;
import com.spt.bas.server.rocketmq.tags.CommonTagsEnum;
import com.spt.bas.server.rocketmq.tags.ContractTagsEnum;
import com.spt.bas.server.rocketmq.tags.WorkTargetTagsEnum;
import com.spt.bas.server.rocketmq.util.RocketmqSendCallbackBuilder;
import com.spt.bas.server.rocketmq.util.ThreadPoolExecutorEngine;
import com.spt.bas.server.service.IMqSyncService;
import com.spt.pm.dao.PmApproveDao;
import com.spt.pm.entity.PmApprove;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * MQ data-sync orchestration service implementation.
 *
 * <p>Phase 6 (06-04) — D-P6-11 refactor target for {@code MQApi}. Each method body is a verbatim
 * copy of the corresponding {@code Synchronized*Task} handler method body (preserved per the
 * "behavior-equivalent dual entry point" semantic — sys_job async cron via the Synchronized*Task
 * handler + MQApi HTTP sync via this service share equivalent business logic).
 *
 * <p>Translation convention (vs the Synchronized*Task source): the {@code ThreadQuery} inner-class
 * dispatch pattern is replaced with equivalent {@code Runnable} lambdas (same behavior — Runnable
 * lambda captures the loop index, compiler generates equivalent bytecode to an anonymous inner
 * class). This avoids 8 same-named {@code ThreadQuery} inner classes colliding inside this single
 * impl. All private helper methods ({@code getCtrContract}, {@code getCtrProduct}, etc.) are
 * preserved verbatim from source.
 *
 * <p>Shared dependencies (all {@code @Autowired} here): 8 DAOs + {@link RocketMQTemplate} +
 * {@link RocketmqCustomProperties}. The Synchronized*Task handlers each declare only the subset
 * they need; this aggregator declares the union.
 *
 * @author Phase 6 (06-04)
 */
@Service
public class MqSyncServiceImpl implements IMqSyncService {

    private static final Logger log = LoggerFactory.getLogger(MqSyncServiceImpl.class);

    /**
     * 查询数据
     */
    public static final int PAGE_COUNT = 100;

    @Autowired
    private CtrContractDao ctrContractDao;
    @Autowired
    private CtrProductDao ctrProductDao;
    @Autowired
    private WorkTargetDao workTargetDao;
    @Autowired
    private CtrContractOphisDao ctrContractOphisDao;
    @Autowired
    private BsCompanyDao bsCompanyDao;
    @Autowired
    private ApplyMatchDao applyMatchDao;
    @Autowired
    private ApplyMatchDetailDao applyMatchDetailDao;
    @Autowired
    private PmApproveDao pmApproveDao;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    @Autowired
    private RocketmqCustomProperties rocketmqCustomProperties;

    // ============================================================
    // SynchronizedCtrContractTask (contractTopic:ALL_CONTRACT)
    // ============================================================

    @Override
    public void synchronizedAllCtrContract() {
        log.info("同步数据中台合同数据定时任务开始======>");
        ThreadPoolExecutor engine = ThreadPoolExecutorEngine.getInstance();
        Integer totalCount = ctrContractDao.selectAllCount();
        // 计算要查多少次
        int pages = totalCount % PAGE_COUNT == 0 ? totalCount / PAGE_COUNT : totalCount / PAGE_COUNT + 1;
        // 加入任务列表
        for (int pageIndex = 0; pageIndex < pages; pageIndex++) {
            final int idx = pageIndex;
            engine.execute(() -> getCtrContract(idx, PAGE_COUNT));
        }
        //ThreadPoolExecutorEngine.shutdown();
        log.info("同步数据中台 t_ctr_contract 数据定时任务 执行成功!");
    }

    private void getCtrContract(int pageIndex, int perPageCount) {
        Pageable page = PageRequest.of(pageIndex, perPageCount);
        Page<CtrContract> pageData = ctrContractDao.findAll(page);
        List<CtrContract> ctrContractList = pageData.get().collect(Collectors.toList());
        log.info("查询了数据数量为---{}", ctrContractList.size());
        String topic = rocketmqCustomProperties.getContractTopic() + ":" + ContractTagsEnum.ALL_CONTRACT.name();
        log.info("MQ主题为---{}", topic);
        for (CtrContract ctrContract : ctrContractList) {
            String messageBody = JSONUtil.toJsonStr(ctrContract);
            // 发送使用带key的消息，使用RocketMQ自带的 Message，设置key的方法行不通！！！
            String key = StringUtils.isBlank(ctrContract.getContractType()) ? ctrContract.getContractNo() : ctrContract.getContractType() + "-" + ctrContract.getContractNo();
            Message<String> message = MessageBuilder.withPayload(messageBody).setHeader("KEYS", key).build();
            rocketMQTemplate.asyncSend(topic, message, RocketmqSendCallbackBuilder.commonCallback());
        }
    }

    // ============================================================
    // SynchronizedCtrProductTask (ctrProduct:PRODUCT)
    // ============================================================

    @Override
    public void synchronizedAllCtrProduct() {
        log.info("同步数据中台合同数据定时任务开始======>");
        ThreadPoolExecutor engine = ThreadPoolExecutorEngine.getInstance();
        Integer totalCount = ctrProductDao.selectAllCount();
        // 计算要查多少次
        int pages = totalCount % PAGE_COUNT == 0 ? totalCount / PAGE_COUNT : totalCount / PAGE_COUNT + 1;
        // 加入任务列表
        for (int pageIndex = 0; pageIndex < pages; pageIndex++) {
            final int idx = pageIndex;
            engine.execute(() -> getCtrProduct(idx, PAGE_COUNT));
        }
        //ThreadPoolExecutorEngine.shutdown();
        log.info("同步数据中台 t_ctr_product 数据定时任务 执行成功!");
    }

    private void getCtrProduct(int pageIndex, int perPageCount) {
        Pageable page = PageRequest.of(pageIndex, perPageCount);
        Page<CtrProduct> pageData = ctrProductDao.findAll(page);
        List<CtrProduct> ctrProducts = pageData.get().collect(Collectors.toList());
        log.info("查询了数据数量为---{}", ctrProducts.size());
        String topic = rocketmqCustomProperties.getCtrProduct() + ":" + ContractTagsEnum.PRODUCT.name();
        log.info("MQ主题为---{}", topic);
        for (CtrProduct ctrProduct : ctrProducts) {
            String messageBody = JSONUtil.toJsonStr(ctrProduct);
            // 发送使用带key的消息，使用RocketMQ自带的 Message，设置key的方法行不通！！！
            Message<String> message = MessageBuilder.withPayload(messageBody).build();
            rocketMQTemplate.asyncSend(topic, message, RocketmqSendCallbackBuilder.commonCallback());
        }
    }

    // ============================================================
    // SynchronizedWorkTargetTask (workTargetTopic:ALL) + testSendMessage (1:1)
    // ============================================================

    @Override
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
            sendWorkTargetList(workTargetList, topic);
        }
        log.info("synchronizedAllWorkTarget同步结束！");
        log.info("同步数据中台 work_target 数据定时任务 执行成功!");
    }

    /**
     * 发送消息
     *
     * @param workTargetList 数据
     */
    private void sendWorkTargetList(List<WorkTarget> workTargetList, String topic) {
        for (WorkTarget workTarget : workTargetList) {
            String messageBody = JSONUtil.toJsonStr(workTarget);
            rocketMQTemplate.asyncSend(topic, messageBody, RocketmqSendCallbackBuilder.commonCallback());
        }
    }

    @Override
    public void testSendMessage() {
        log.info("测试同步消息发送！");
        //String topic = rocketmqCustomProperties.getWorkTargetTopic() + ":" + WorkTargetTagsEnum.ALL.name();
        Message<String> message = MessageBuilder.withPayload("这个是测试消息").build();
        rocketMQTemplate.send("yyc-data", message);
        log.info("测试同步消息发送完成！");
    }

    // ============================================================
    // SynchronizedCtrContractOphisTask (contractHistoryTopic:OPHIS)
    // ============================================================

    @Override
    public void synchronizedAllCtrContractOphis() {
        log.info("同步数据中台合同历史数据定时任务开始======>");
        ThreadPoolExecutor engine = ThreadPoolExecutorEngine.getInstance();
        Integer totalCount = ctrContractOphisDao.selectAllCount();
        // 计算要查多少次
        int pages = totalCount % PAGE_COUNT == 0 ? totalCount / PAGE_COUNT : totalCount / PAGE_COUNT + 1;
        // 加入任务列表
        for (int pageIndex = 0; pageIndex < pages; pageIndex++) {
            final int idx = pageIndex;
            engine.execute(() -> getCtrContractOphis(idx, PAGE_COUNT));
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

    // ============================================================
    // SynchronizedBsCompanyTask (companyTopic:ALL)
    // ============================================================

    @Override
    public void synchronizedAllBsCompany() {
        String topic = rocketmqCustomProperties.getCompanyTopic() + ":" + BsCompanyTagsEnum.ALL.name();
        Integer totalCount = bsCompanyDao.selectAllCount();
        int pages = totalCount % PAGE_COUNT == 0 ? totalCount / PAGE_COUNT : totalCount / PAGE_COUNT + 1;
        log.info("MQ主题为---{}", topic);
        for (int pageIndex = 0; pageIndex < pages; pageIndex++) {
            Pageable page = PageRequest.of(pageIndex, PAGE_COUNT);
            Page<BsCompany> pageData = bsCompanyDao.findAll(page);
            List<BsCompany> companyList = pageData.get().collect(Collectors.toList());
            sendBsCompanyList(companyList, topic);
        }
        log.info("同步数据中台 t_bs_company 数据定时任务 执行成功!");
    }

    /**
     * 发送消息
     *
     * @param companyList 数据
     */
    private void sendBsCompanyList(List<BsCompany> companyList, String topic) {
        for (BsCompany company : companyList) {
            String messageBody = JSONUtil.toJsonStr(company);
            rocketMQTemplate.asyncSend(topic, messageBody, RocketmqSendCallbackBuilder.commonCallback());
        }
    }

    // ============================================================
    // SynchronizedApplyMatchTask (commonTopic:APPLY_MATCH)
    // ============================================================

    @Override
    public void synchronizedAllApplyMatch() {
        log.info("同步数据中台合同撮合表定时任务开始======>");
        ThreadPoolExecutor engine = ThreadPoolExecutorEngine.getInstance();
        Integer totalCount = applyMatchDao.selectAllCount();
        // 计算要查多少次
        int pages = totalCount % PAGE_COUNT == 0 ? totalCount / PAGE_COUNT : totalCount / PAGE_COUNT + 1;
        // 加入任务列表
        for (int pageIndex = 0; pageIndex < pages; pageIndex++) {
            final int idx = pageIndex;
            engine.execute(() -> getApplyMatch(idx, PAGE_COUNT));
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

    // ============================================================
    // SynchronizedApplyMatchDetailTask (commonTopic:APPLY_MATCH_DETAIL)
    // ============================================================

    @Override
    public void synchronizedAllApplyMatchDetail() {
        log.info("同步数据中台合同撮合表详情表定时任务开始======>");
        ThreadPoolExecutor engine = ThreadPoolExecutorEngine.getInstance();
        Integer totalCount = applyMatchDetailDao.selectAllCount();
        // 计算要查多少次
        int pages = totalCount % PAGE_COUNT == 0 ? totalCount / PAGE_COUNT : totalCount / PAGE_COUNT + 1;
        // 加入任务列表
        for (int pageIndex = 0; pageIndex < pages; pageIndex++) {
            final int idx = pageIndex;
            engine.execute(() -> getApplyMatchDetail(idx, PAGE_COUNT));
        }
        //ThreadPoolExecutorEngine.shutdown();
        log.info("同步数据中台 t_apply_match_detail 数据定时任务 执行成功!");
    }

    private void getApplyMatchDetail(int pageIndex, int perPageCount) {
        Pageable page = PageRequest.of(pageIndex, perPageCount);
        Page<ApplyMatchDetail> pageData = applyMatchDetailDao.findAll(page);
        List<ApplyMatchDetail> applyMatches = pageData.get().collect(Collectors.toList());
        log.info("查询了数据数量为---{}", applyMatches.size());
        String topic = rocketmqCustomProperties.getCommonTopic() + ":" + CommonTagsEnum.APPLY_MATCH_DETAIL.name();
        log.info("MQ主题为---{}", topic);
        for (ApplyMatchDetail applyMatch : applyMatches) {
            String messageBody = JSONUtil.toJsonStr(applyMatch);
            // 发送使用带key的消息，使用RocketMQ自带的 Message，设置key的方法行不通！！！
            Message<String> message = MessageBuilder.withPayload(messageBody).build();
            rocketMQTemplate.asyncSend(topic, message, RocketmqSendCallbackBuilder.commonCallback());
        }
    }

    // ============================================================
    // SynchronizedPmApproveTask (commonTopic:PM_APPROVE)
    // ============================================================

    @Override
    public void synchronizedAllPmApprove() {
        log.info("同步数据中台审批表定时任务开始======>");
        ThreadPoolExecutor engine = ThreadPoolExecutorEngine.getInstance();
        Integer totalCount = pmApproveDao.selectAllCount();
        // 计算要查多少次
        int pages = totalCount % PAGE_COUNT == 0 ? totalCount / PAGE_COUNT : totalCount / PAGE_COUNT + 1;
        // 加入任务列表
        for (int pageIndex = 0; pageIndex < pages; pageIndex++) {
            final int idx = pageIndex;
            engine.execute(() -> getPmApprove(idx, PAGE_COUNT));
        }
        //ThreadPoolExecutorEngine.shutdown();
        log.info("同步数据中台 t_pm_approve 数据定时任务 执行成功!");
    }

    private void getPmApprove(int pageIndex, int perPageCount) {
        Pageable page = PageRequest.of(pageIndex, perPageCount);
        Page<PmApprove> pageData = pmApproveDao.findAll(page);
        List<PmApprove> pmApproves = pageData.get().collect(Collectors.toList());
        log.info("查询了数据数量为---{}", pmApproves.size());
        String topic = rocketmqCustomProperties.getCommonTopic() + ":" + CommonTagsEnum.PM_APPROVE.name();
        log.info("MQ主题为---{}", topic);
        for (PmApprove pmApprove : pmApproves) {
            String messageBody = JSONUtil.toJsonStr(pmApprove);
            Message<String> message = MessageBuilder.withPayload(messageBody).build();
            rocketMQTemplate.asyncSend(topic, message, RocketmqSendCallbackBuilder.commonCallback());
        }
    }
}
