package com.spt.bas.server.service.impl;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyAgreementVirtual;
import com.spt.bas.client.entity.StockVirtual;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.ApplyAgreementVirtualDao;
import com.spt.bas.server.dao.StockVirtualDao;
import com.spt.bas.server.service.IApplyAgreementVirtualService;
import com.spt.bas.server.util.BasBusinessUtil;
import com.spt.bas.server.util.SubjectUtil;
import com.spt.pm.dao.PmApproveDao;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.inter.IPmService;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.number.NumberUtil;
import com.spt.tools.data.annotation.ServiceTransactional;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 协议采购申请
 *
 * @Author MoonLight
 * @Date 2024/8/19 15:40
 * @Version 1.0
 */
@Transactional(readOnly = true)
@Component("applyAgreementVirtualService")
public class ApplyAgreementVirtualServiceImpl extends BaseService<ApplyAgreementVirtual> implements IApplyAgreementVirtualService, IPmService, IPmApproveListener {
    @Resource
    private ApplyAgreementVirtualDao applyAgreementVirtualDao;
    @Resource
    private StockVirtualDao stockVirtualDao;
    @Resource
    private PmApproveDao pmApproveDao;

    private static final String PREFIX_XYB = "XYB";

    @Override
    public BaseDao<ApplyAgreementVirtual> getBaseDao() {
        return applyAgreementVirtualDao;
    }

    /**
     * 执行审批步骤
     *
     * @param approve
     * @param nextStep
     */
    @Override
    @ServerTransactional
    public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
        if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
            ApplyAgreementVirtual entity = applyAgreementVirtualDao.findOne(approve.getBizId());
            List<StockVirtual> stockVirtualList = stockVirtualDao.findBizStockVirtual(entity.getId(), BasConstants.STOCK_VIRTUAL_XY);
            if (CollectionUtils.isNotEmpty(stockVirtualList)) {
                stockVirtualList.forEach(stockVirtual -> stockVirtual.setVirtualStatus(BasConstants.STOCK_VIRTUAL_STATUS_C));
                stockVirtualDao.saveAll(stockVirtualList);
            }
            StockVirtual stockVirtual = new StockVirtual();
            BeanUtils.copyProperties(entity, stockVirtual,"id");
            stockVirtual.setBizApplyVirtualId(entity.getId());
            stockVirtual.setVirtualBuyType(BasConstants.STOCK_VIRTUAL_XY);
            stockVirtual.setVirtualStatus(BasConstants.STOCK_VIRTUAL_STATUS_N);
            stockVirtual.setPublishTime(new Date());
            stockVirtualDao.save(stockVirtual);
        }
    }

    /**
     * 审批撤回
     *
     * @param vo
     */
    @Override
    public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {}


    @Override
    @ServerTransactional
    public IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException {
        if (pmEntity instanceof ApplyAgreementVirtual) {
            ApplyAgreementVirtual entity = (ApplyAgreementVirtual) pmEntity;
            if (Objects.isNull(entity.getId())
                    || entity.getId() == 0L
                    || StringUtils.equals(BasConstants.APPROVE_STATUS_C, entity.getStatus())) {
                String suffix = BasBusinessUtil.composeContractNoSuffix(entity.getEnterpriseId());
                entity.setStockVirtualNo(PREFIX_XYB + suffix);
            }
            entity = applyAgreementVirtualDao.save(entity);
            return entity;
        }
        return null;
    }

    /**
     * 标题
     *
     * @param pmEntity
     * @param pmProcess
     */
    @Override
    public String getSubject(IPmEntity pmEntity, PmProcess pmProcess) {
        if (pmEntity instanceof ApplyAgreementVirtual) {
            ApplyAgreementVirtual entity = (ApplyAgreementVirtual) pmEntity;
            String stockVirtualNo = entity.getStockVirtualNo();
            String productName = entity.getProductName();
            String factoryName = entity.getFactoryName();
            String companyName = entity.getCompanyName();
            String matchUserName = entity.getMatchUserName();
            String dealPriceStr = NumberUtil.formatNumber(entity.getDealPrice(), "#.##");
            String dealNumberStr = NumberUtil.formatNumber(entity.getDealNumber(), "#.##");
            String totalAmountStr = NumberUtil.formatNumber(entity.getTotalAmount(), "#.##");
            return SubjectUtil.formatSubject("[协议采购]", stockVirtualNo, productName, factoryName,
                    companyName, "单价" + dealPriceStr, "数量" + dealNumberStr, "采购总价" + totalAmountStr, matchUserName);
        }
        return "";
    }

    @Override
    @ServiceTransactional
    public void updateFileId(Long id, String fileId) {
        applyAgreementVirtualDao.updateContentTemplateId(id, fileId);
    }
}
