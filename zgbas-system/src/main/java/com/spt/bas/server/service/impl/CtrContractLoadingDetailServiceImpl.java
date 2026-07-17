package com.spt.bas.server.service.impl;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.CtrContractLoading;
import com.spt.bas.client.entity.CtrContractLoadingDetail;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.CtrContractLoadingDao;
import com.spt.bas.server.dao.CtrContractLoadingDetailDao;
import com.spt.bas.server.service.ICtrContractLoadingDetailService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: gaojy
 * @create 2022/6/20 9:44
 * @version: 1.0
 * @description:
 */
@Component
@Transactional(readOnly = true)
public class CtrContractLoadingDetailServiceImpl extends BaseService<CtrContractLoadingDetail> implements ICtrContractLoadingDetailService {

    @Autowired
    private CtrContractLoadingDetailDao contractLoadingDetailDao;
    @Autowired
    private CtrContractLoadingDao ctrContractLoadingDao;

    @Override
    public BaseDao<CtrContractLoadingDetail> getBaseDao() {
        return contractLoadingDetailDao;
    }

    @Override
    @ServerTransactional
    public void saveLoadingDetails(List<CtrContractLoadingDetail> insertedRecords, List<CtrContractLoadingDetail> updatedRecords,
                                   List<CtrContractLoadingDetail> deletedRecords, Long loadingId, Boolean initDetailIdFlg) {
        CtrContractLoading loading = ctrContractLoadingDao.findOne(loadingId);
        List<CtrContractLoadingDetail> detailList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(insertedRecords)){
            for (CtrContractLoadingDetail loadingData : insertedRecords) {
                CtrContractLoadingDetail insertDetail = saveData(loadingData, loading, initDetailIdFlg);
                detailList.add(insertDetail);
            }
        }
        if (CollectionUtils.isNotEmpty(updatedRecords)){
            for (CtrContractLoadingDetail loadingData : updatedRecords) {
                CtrContractLoadingDetail updateDetail = saveData(loadingData, loading, initDetailIdFlg);
                detailList.add(updateDetail);
            }
        }
        if (CollectionUtils.isNotEmpty(deletedRecords)){
            contractLoadingDetailDao.deleteAll(deletedRecords);
        }
        CtrContractLoading newLoading = ctrContractLoadingDao.findOne(loadingId);
        List<CtrContractLoadingDetail> loadingDetails = newLoading.getLoadingDetails();
        if (CollectionUtils.isNotEmpty(loadingDetails)){
            detailList.addAll(loadingDetails);
        }
        detailList = detailList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(CtrContractLoadingDetail::getId))), ArrayList::new));
        if (CollectionUtils.isNotEmpty(detailList)) {
            newLoading.setProductName(detailList.stream().map(CtrContractLoadingDetail::getProductName).distinct().collect(Collectors.joining(BasConstants.COMMA)));
            newLoading.setBrandNumber(detailList.stream().map(CtrContractLoadingDetail::getBrandNumber).distinct().collect(Collectors.joining(BasConstants.COMMA)));
            newLoading.setFactoryName(detailList.stream().map(CtrContractLoadingDetail::getFactoryName).distinct().collect(Collectors.joining(BasConstants.COMMA)));
            newLoading.setDealNumber(detailList.stream().map(CtrContractLoadingDetail::getDealNumber).reduce(BigDecimal.ZERO, BigDecimal::add));
            newLoading.setPlateNumber(detailList.stream().map(CtrContractLoadingDetail::getPlateNumber).distinct().collect(Collectors.joining(BasConstants.COMMA)));
            newLoading.setDriverName(detailList.stream().map(CtrContractLoadingDetail::getDriverName).distinct().collect(Collectors.joining(BasConstants.COMMA)));
            newLoading.setDriverCardNo(detailList.stream().map(CtrContractLoadingDetail::getDriverCardNo).distinct().collect(Collectors.joining(BasConstants.COMMA)));
            ctrContractLoadingDao.save(newLoading);
        }
    }

    @Override
    @ServerTransactional
    public void deleteLoadingDetail(Long id) {
        contractLoadingDetailDao.delete(id);
    }

    private CtrContractLoadingDetail saveData(CtrContractLoadingDetail loadingDetail, CtrContractLoading loading, Boolean initDetailIdFlg) {
        loadingDetail.setLoading(loading);
        loadingDetail.setUpdatedDate(new Date());
        loadingDetail.setCreatedDate(new Date());
        if (Boolean.TRUE.equals(initDetailIdFlg)){
            loadingDetail.setId(0L);
        }
        return contractLoadingDetailDao.save(loadingDetail);
    }
}
