package com.spt.bas.server.service.impl;

import cn.hutool.core.thread.ExecutorBuilder;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsCompanyDcsx;
import com.spt.bas.client.entity.FundAmountFlow;
import com.spt.bas.client.vo.WsMessage;
import com.spt.bas.server.dao.BsCompanyDcsxDao;
import com.spt.bas.server.dao.fund.FundAmountFlowDao;
import com.spt.bas.server.enums.FundFlowEnum;
import com.spt.bas.server.service.IFundAmountFlowService;
import com.spt.bas.server.util.HttpUtil;
import com.spt.bas.server.util.SubjectUtil;
import com.spt.pm.entity.PmApprove;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.annotation.ServiceTransactional;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author MoonLight
 * @Date 2024/7/12 17:51
 * @Version 1.0
 */
@Component
@Transactional(readOnly = true)
public class FundAmountFlowServiceImpl extends BaseService<FundAmountFlow> implements IFundAmountFlowService {
    ExecutorService executor = ExecutorBuilder.create()
            .setCorePoolSize(4)
            .setMaxPoolSize(10)
            .setWorkQueue(new LinkedBlockingQueue<>(100))
            .build();
    @Resource
    private FundAmountFlowDao fundAmountFlowDao;
    @Resource
    private BsCompanyDcsxDao bsCompanyDcsxDao;

    @Override
    public BaseDao<FundAmountFlow> getBaseDao() {
        return fundAmountFlowDao;
    }

    /**
     * 记录资金代采方金额流水
     *
     * @param fundCompanyName 资金方
     * @param flowAmount      流水金额
     * @param fundFlowEnum    流水类型
     * @param linkApprove     关联审批单
     * @return fundAmountFlow 资金流水记录
     */
    @Override
    @ServiceTransactional
    public synchronized FundAmountFlow addFundFlow(String fundCompanyName, String ourCompanyName, BigDecimal flowAmount, FundFlowEnum fundFlowEnum, PmApprove linkApprove) throws ApplicationException {
        // 1. 检查输入参数
        if (StringUtils.isBlank(fundCompanyName)) {
            throw new ApplicationException("资金方不可为空");
        }
        if (Objects.isNull(flowAmount)) {
            throw new ApplicationException("流水金额不可为空");
        }
        if (Objects.isNull(fundFlowEnum)) {
            throw new ApplicationException("流水类型不可为空");
        }
        if (Objects.isNull(linkApprove)) {
            throw new ApplicationException("关联审批单不可为空");
        }

        // 2. 获取目标公司信息
        BsCompanyDcsx targetCompany = bsCompanyDcsxDao.findByCompanyName(fundCompanyName);
        if (Objects.isNull(targetCompany)) {
            throw new ApplicationException("未查询到资金方: " + fundCompanyName);
        }

        // 3. 记录当前资金金额
        BigDecimal fundAmount = BigDecimal.ZERO;
        if (StringUtils.equals(BasConstants.COMPANY_NAME_QDZG, ourCompanyName)) {
            fundAmount = Objects.isNull(targetCompany.getFundAmountQg()) ? BigDecimal.ZERO : targetCompany.getFundAmountQg();
        } else if (StringUtils.equals(BasConstants.COMPANY_NAME_WSNB, ourCompanyName)) {
            fundAmount = Objects.isNull(targetCompany.getFundAmountWs()) ? BigDecimal.ZERO : targetCompany.getFundAmountWs();
        }
        logger.info("targetCompanyName:{}, flowType:{}, fundAmount:{}", fundCompanyName, fundFlowEnum.getFlowName(), fundAmount);

        // 4. 创建并保存资金流水记录
        FundAmountFlow flow = new FundAmountFlow();
        flow.setFundCompanyId(targetCompany.getId());
        flow.setFlowType(fundFlowEnum.getFlowType());
        flow.setFlowAmount(flowAmount);
        flow.setInitialAmount(fundAmount);
        flow.setUltimateAmount(fundAmount.add(flowAmount));
        flow.setSubject(linkApprove.getSubject());
        flow.setLinkApproveId(linkApprove.getId());
        flow.setOurCompanyName(ourCompanyName);
        flow = fundAmountFlowDao.save(flow);

        // 5. 更新目标公司的资金金额
        if (StringUtils.equals(BasConstants.COMPANY_NAME_QDZG, ourCompanyName)) {
            targetCompany.setFundAmountQg(flow.getUltimateAmount());
        } else if (StringUtils.equals(BasConstants.COMPANY_NAME_WSNB, ourCompanyName)) {
            targetCompany.setFundAmountWs(flow.getUltimateAmount());
        }
        bsCompanyDcsxDao.save(targetCompany);
        this.notifyFundMessage(targetCompany);
        return flow;
    }

    /**
     * 资金方，金额更新发送通知消息给前端页面
     * @param targetCompany
     */
    private void notifyFundMessage(BsCompanyDcsx targetCompany) {
        executor.execute(() -> {
            try {
                BigDecimal fundAmountQg = targetCompany.getFundAmountQg();
                if (fundAmountQg == null) {
                    fundAmountQg = BigDecimal.ZERO;
                }
                BigDecimal fundAmountWs = targetCompany.getFundAmountWs();
                if (fundAmountWs == null) {
                    fundAmountWs = BigDecimal.ZERO;
                }
                String fundAmountStr = SubjectUtil.formatMoney(fundAmountQg.add(fundAmountWs), "");
                String fundAmountQgStr = SubjectUtil.formatMoney(fundAmountQg, "");
                String fundAmountWsStr = SubjectUtil.formatMoney(fundAmountWs, "");
                WsMessage wsMessage = new WsMessage(targetCompany.getId(), WsMessage.MESSAGE_TYPE_F, targetCompany.getCompanyName(), fundAmountStr,fundAmountQgStr,fundAmountWsStr);
                String result = HttpUtil.doPostJson("http://localhost:82/open/wsMessage/notifyWsMessage", JsonUtil.obj2Json(wsMessage));
                logger.info("notifyFundMessage result:{}", result);
            } catch (Exception e) {
                logger.error("notifyFundMessage error:", e);
            }
        });
    }
}
