package com.spt.bas.server.service;

import com.spt.bas.client.entity.CtrContractLoadingDetail;
import com.spt.tools.jpa.service.IBaseService;

import java.util.List;

/**
 * @Author: gaojy
 * @create 2022/6/20 9:42
 * @version: 1.0
 * @description:
 */
public interface ICtrContractLoadingDetailService extends IBaseService<CtrContractLoadingDetail> {

    void saveLoadingDetails(List<CtrContractLoadingDetail> insertedRecords, List<CtrContractLoadingDetail> updatedRecords, List<CtrContractLoadingDetail> deletedRecords, Long loadingId, Boolean initDetailIdFlg);

    void deleteLoadingDetail(Long id);
}
