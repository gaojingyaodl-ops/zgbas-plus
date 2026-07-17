package com.spt.bas.server.service;

import com.spt.bas.client.entity.ApplyMatchChain;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.service.IBaseService;

import java.util.List;

/**
 * 代采赊销中间链条表
 * @Author: gaojy
 * @create 2022/9/14 10:41
 * @version: 1.0
 * @description:
 */
public interface IApplyMatchChainService extends IBaseService<ApplyMatchChain> {

    void updateChainApproveId(Long applyMatchId, Long approveId);

    List<ApplyMatchChain> findMatchChains(Long applyMatchId);

    void saveChainDetails(List<ApplyMatchChain> insertedRecords, List<ApplyMatchChain> updatedRecords, List<ApplyMatchChain> deletedRecords, Long approveId, Long applyMatchId) throws ApplicationException;
}
