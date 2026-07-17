package com.spt.bas.server.rocketmq.listener;

import cn.hutool.json.JSONUtil;
import com.spt.bas.client.entity.*;
import com.spt.bas.server.dao.*;
import com.spt.bas.server.event.CtrContractEvent;
import com.spt.bas.server.rocketmq.RocketmqCustomProperties;
import com.spt.bas.server.rocketmq.tags.CommonTagsEnum;
import com.spt.bas.server.rocketmq.tags.ContractTagsEnum;
import com.spt.bas.server.rocketmq.util.RocketmqSendCallbackBuilder;
import com.spt.pm.dao.PmApproveDao;
import com.spt.pm.entity.PmApprove;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2023/3/2 13:55
 */
@Slf4j
@Component
public class CtrContractTransactionalListener {

    @Autowired
    private CtrContractDao ctrContractDao;
    @Autowired
    private CtrProductDao ctrProductDao;
    @Autowired
    private CtrContractOphisDao contractOphisDao;
    @Autowired
    private PmApproveDao pmApproveDao;
    @Autowired
    private ApplyMatchDao applyMatchDao;
    @Autowired
    private ApplyMatchDetailDao applyMatchDetailDao;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    @Autowired
    private RocketmqCustomProperties rocketmqCustomProperties;

    /**
     * 合同变更事务提交后事件
     *
     * @param contractEvent 事件
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void ctrContractEvent(CtrContractEvent contractEvent) {
        if (Objects.isNull(contractEvent) || Objects.isNull(contractEvent.getCtrContractId())) {
            log.error("合同新增事务提交后发送消息失败（无法获取合同实例）");
            return;
        }
        Optional<CtrContract> optional = ctrContractDao.findById(contractEvent.getCtrContractId());
        if (optional.isPresent()) {
            CtrContract ctrContract = optional.get();
            // 推送合同产品详情数据
            sendCtrProduct(ctrContract.getId());
            // 推送单个合同历史表信息
            sendContractOphis(ctrContract.getId());
            // 推送审批表数据
            sendPmApprove(ctrContract.getApproveId());
            // 推送撮合表
            sendApplyMatch(ctrContract.getApproveId());
            // 推送撮合表详情表
            sendApplyMatchDetail(ctrContract.getContractNo());
            log.info("同步数据中台，合同号为----------{}", ctrContract.getContractNo());
            String messageBody = JSONUtil.toJsonStr(ctrContract);
            String topic = rocketmqCustomProperties.getContractTopic() + ":" + ContractTagsEnum.SIGN.name();
            log.info("主题为------{}", topic);
            String key = StringUtils.isBlank(ctrContract.getContractType()) ? ctrContract.getContractNo() : ctrContract.getContractType() + "-" + ctrContract.getContractNo();
            Message<String> message = MessageBuilder.withPayload(messageBody).setHeader("KEYS", key).build();
            rocketMQTemplate.asyncSend(topic, message, RocketmqSendCallbackBuilder.commonCallback());
        } else {
            log.error("无法获取到合同信息，同步数据中台失败！");
        }
    }

    /**
     * 推送合同产品详情表 t_ctr_product
     *
     * @param contractId 合同id
     */
    private void sendCtrProduct(Long contractId) {
        List<CtrProduct> productList = ctrProductDao.findByCtrContractId(contractId);
        if (CollectionUtils.isNotEmpty(productList)) {
            String productTopic = rocketmqCustomProperties.getCtrProduct() + ":" + ContractTagsEnum.PRODUCT_SIGN.name();
            for (CtrProduct ctrProduct : productList) {
                log.info("同步合同产品详情表，id为：{}", ctrProduct.getId());
                String messageProduct = JSONUtil.toJsonStr(ctrProduct);
                Message<String> product = MessageBuilder.withPayload(messageProduct).build();
                rocketMQTemplate.asyncSend(productTopic, product, RocketmqSendCallbackBuilder.commonCallback());
            }
        } else {
            log.error("合同产品详情同步失败！合同id为：{}", contractId);
        }
    }

    /**
     * 推送审批表表 t_pm_approve
     *
     * @param approveId 审批id
     */
    private void sendPmApprove(Long approveId) {
        if (Objects.isNull(approveId)) {
            log.error("审批表同步数据中台失败！原因approveId为空！");
            return;
        }
        Optional<PmApprove> approve = pmApproveDao.findById(approveId);
        if (approve.isPresent()) {
            PmApprove pmApprove = approve.get();
            String topic = rocketmqCustomProperties.getCommonTopic() + ":" + CommonTagsEnum.PM_APPROVE_SIGN.name();
            log.info("审批表详情表，id为：{}", approveId);
            String messageProduct = JSONUtil.toJsonStr(pmApprove);
            Message<String> pmApproveMessage = MessageBuilder.withPayload(messageProduct).build();
            rocketMQTemplate.asyncSend(topic, pmApproveMessage, RocketmqSendCallbackBuilder.commonCallback());
        } else {
            log.error("审批表同步失败！审批id为：{}", approveId);
        }
    }

    /**
     * 推送撮合表 t_apply_match
     *
     * @param approveId 审批id
     */
    private void sendApplyMatch(Long approveId) {
        if (Objects.isNull(approveId)) {
            log.error("sendApplyMatchDetail同步数据中台失败！原因approveId为空！");
            return;
        }
        ApplyMatch applyMatch = applyMatchDao.findByApproveId(approveId);
        if (Objects.nonNull(applyMatch)) {
            String topic = rocketmqCustomProperties.getCommonTopic() + ":" + CommonTagsEnum.APPLY_MATCH_SIGN.name();
            log.info("同步撮合表，id为：{}", applyMatch.getId());
            String messageProduct = JSONUtil.toJsonStr(applyMatch);
            Message<String> message = MessageBuilder.withPayload(messageProduct).build();
            rocketMQTemplate.asyncSend(topic, message, RocketmqSendCallbackBuilder.commonCallback());
        } else {
            log.error("同步撮合表失败！审批id为：{}", approveId);
        }
    }

    /**
     * 推送撮合表详情表 t_apply_match
     *
     * @param contractNo 合同编号
     */
    private void sendApplyMatchDetail(String contractNo) {
        if (StringUtils.isBlank(contractNo)) {
            log.error("撮合详情表同步数据中台失败！原因是contractNo为空！");
            return;
        }
        ApplyMatchDetail applyMatchDetail = applyMatchDetailDao.findByContractNo(contractNo);
        if (Objects.nonNull(applyMatchDetail)) {
            String topic = rocketmqCustomProperties.getCommonTopic() + ":" + CommonTagsEnum.APPLY_MATCH_DETAIL_SIGN.name();
            log.info("同步撮合表详情表，id为：{}", applyMatchDetail.getId());
            String messageProduct = JSONUtil.toJsonStr(applyMatchDetail);
            Message<String> message = MessageBuilder.withPayload(messageProduct).build();
            rocketMQTemplate.asyncSend(topic, message, RocketmqSendCallbackBuilder.commonCallback());
        } else {
            log.error("撮合表详情表同步失败！合同编号为：{}", contractNo);
        }
    }

    /**
     * 推送合同历史表 t_ctr_contract_ophis
     *
     * @param contractId 合同id
     */
    private void sendContractOphis(Long contractId) {
        if (Objects.isNull(contractId)) {
            log.error("合同历史表同步数据中台失败！原因是contractId为空！");
            return;
        }
        List<CtrContractOphis> list = new ArrayList<>();
        try {
            list = contractOphisDao.findByContractId(contractId);
        } catch (Exception e) {
            log.error("查询合同历史信息错误！{}", e.getMessage());
        }
        if (CollectionUtils.isNotEmpty(list)) {
            String topic = rocketmqCustomProperties.getContractHistoryTopic() + ":" + ContractTagsEnum.OPHIS_SIGN.name();
            for (CtrContractOphis ctrContractOphis : list) {
                log.info("合同历史表，id为：{}", ctrContractOphis.getId());
                String messageProduct = JSONUtil.toJsonStr(ctrContractOphis);
                Message<String> message = MessageBuilder.withPayload(messageProduct).build();
                rocketMQTemplate.asyncSend(topic, message, RocketmqSendCallbackBuilder.commonCallback());
            }
        } else {
            log.error("合同历史表同步失败！合同id为：{}", contractId);
        }
    }

}
