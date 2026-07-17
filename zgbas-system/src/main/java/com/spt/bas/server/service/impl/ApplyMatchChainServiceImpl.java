package com.spt.bas.server.service.impl;

import com.spt.bas.client.entity.ApplyMatchChain;
import com.spt.bas.server.dao.ApplyMatchChainDao;
import com.spt.bas.server.service.IApplyMatchChainService;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.data.annotation.ServiceTransactional;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 代采赊销中间链条表
 * @Author: gaojy
 * @create 2022/9/14 10:42
 * @version: 1.0
 * @description:
 */
@Slf4j
@Component
@Transactional(readOnly = true)
public class ApplyMatchChainServiceImpl extends BaseService<ApplyMatchChain> implements IApplyMatchChainService {

    @Autowired
    private ApplyMatchChainDao applyMatchChainDao;

    @Override
    public BaseDao<ApplyMatchChain> getBaseDao() {
        return applyMatchChainDao;
    }

    @Override
    @ServiceTransactional
    public void updateChainApproveId(Long applyMatchId, Long approveId) {
        applyMatchChainDao.updateChainApproveId(applyMatchId, approveId);
    }

    @Override
    public List<ApplyMatchChain> findMatchChains(Long applyMatchId) {
        return applyMatchChainDao.findByApplyMatchIdOrderBySerialNumberAsc(applyMatchId);
    }

    @Override
    @ServiceTransactional
    public void saveChainDetails(List<ApplyMatchChain> insertedRecords, List<ApplyMatchChain> updatedRecords, List<ApplyMatchChain> deletedRecords, Long approveId, Long applyMatchId) throws ApplicationException {
        List<ApplyMatchChain> chainList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(insertedRecords)){
            for (ApplyMatchChain chain : insertedRecords) {
                chain.setId(0L);
                chain.setApproveId(approveId);
                chain.setApplyMatchId(applyMatchId);
                chain.setUpdatedDate(new Date());
                chain.setCreatedDate(new Date());
                chainList.add(chain);
            }
        }
        if (CollectionUtils.isNotEmpty(updatedRecords)){
            for (ApplyMatchChain chain : updatedRecords) {
                chain.setId(0L);
                chain.setApproveId(approveId);
                chain.setApplyMatchId(applyMatchId);
                chain.setUpdatedDate(new Date());
                chain.setCreatedDate(new Date());
                chainList.add(chain);
            }
        }
        if (CollectionUtils.isNotEmpty(deletedRecords)){
            applyMatchChainDao.deleteAll(deletedRecords);
        }

        chainList = chainList.stream().sorted(Comparator.comparing(ApplyMatchChain::getSerialNumber)).collect(Collectors.toList());
        for (int i = 0; i < chainList.size(); i++) {
            ApplyMatchChain chain = chainList.get(i);
            if (i < chainList.size() - 1) {
                ApplyMatchChain nextChain = chainList.get(i + 1);
                if (chain.getSellDealPrice().compareTo(nextChain.getBuyDealPrice()) != 0) {
                    throw new ApplicationException("中间链条：上家的销售单价必须和下家的采购单价保持一致！");
                }
            }
        }

        if (CollectionUtils.isNotEmpty(chainList)) {
            applyMatchChainDao.saveAll(chainList);
        }
    }
}
