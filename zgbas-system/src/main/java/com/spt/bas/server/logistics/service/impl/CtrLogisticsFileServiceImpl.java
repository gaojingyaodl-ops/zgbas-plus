package com.spt.bas.server.logistics.service.impl;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.LogisticsEnum;
import com.spt.bas.client.entity.CtrLogisticsFile;
import com.spt.bas.client.vo.CtrLogisticsFileRespVo;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.logistics.CtrLogisticsFileDao;
import com.spt.bas.server.logistics.service.ICtrLogisticsFileService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * 合同物流提货附件
 *
 * @Author MoonLight
 * @Date 2023/7/5 16:25
 * @Version 1.0
 */
@Component
public class CtrLogisticsFileServiceImpl extends BaseService<CtrLogisticsFile> implements ICtrLogisticsFileService {

    @Resource
    private CtrLogisticsFileDao ctrLogisticsFileDao;

    @Override
    public BaseDao<CtrLogisticsFile> getBaseDao() {
        return ctrLogisticsFileDao;
    }

    @Override
    @ServerTransactional
    public CtrLogisticsFile saveLogisticsFile(CtrLogisticsFile logisticsFile) {
        String fileId = logisticsFile.getFileId();
        fileId = fileId.endsWith(BasConstants.COMMA) ? fileId : fileId + BasConstants.COMMA;
        if (Objects.isNull(logisticsFile.getLogisticsDeliveryId())) {
            logisticsFile.setId(0L);
            logisticsFile.setFileId(fileId);
            return ctrLogisticsFileDao.save(logisticsFile);
        }
        CtrLogisticsFile entity = ctrLogisticsFileDao.findByLogisticsDeliveryIdAndFileType(logisticsFile.getLogisticsDeliveryId(), logisticsFile.getFileType());
        if (Objects.nonNull(entity)) {
            entity.setFileId(fileId);
            entity.setCfcaContractNo(logisticsFile.getCfcaContractNo());
        } else {
            entity = logisticsFile;
            entity.setId(0L);
        }
        return ctrLogisticsFileDao.save(entity);
    }

    @Override
    public CtrLogisticsFileRespVo findByLogisticsIdAndLogisticsDeliveryId(Long logisticsId,Long logisticsDeliveryId) {
        CtrLogisticsFileRespVo respVo = new CtrLogisticsFileRespVo();
        if (logisticsId == null) {
            return respVo;
        }
        List<CtrLogisticsFile> logisticsFileList = ctrLogisticsFileDao.findByLogisticsIdAndLogisticsDeliveryId(logisticsId,logisticsDeliveryId);
        if(CollectionUtils.isNotEmpty(logisticsFileList)) {
            for (CtrLogisticsFile file : logisticsFileList) {
                if(StringUtils.equalsIgnoreCase(LogisticsEnum.LADING.getLogisticsCode(), file.getFileType())) {
                    respVo.setLadingFileId(file.getFileId());
                    respVo.setLadingApproveId(file.getApproveId());
                } else if(StringUtils.equalsIgnoreCase(LogisticsEnum.DISTRIBUTION.getLogisticsCode(), file.getFileType())) {
                    respVo.setDistributionFileId(file.getFileId());
                    respVo.setDistributionApproveId(file.getApproveId());
                } else if(StringUtils.equalsIgnoreCase(LogisticsEnum.FUND_LADING.getLogisticsCode(), file.getFileType())) {
                    respVo.setFundLadingFileId(file.getFileId());
                    respVo.setFundLadingApproveId(file.getApproveId());
                } else if(StringUtils.equalsIgnoreCase(LogisticsEnum.FUND_DISTRIBUTION.getLogisticsCode(), file.getFileType())) {
                    respVo.setFundDistributionFileId(file.getFileId());
                    respVo.setFundDistributionApproveId(file.getApproveId());
                } 
            }
        } 
        return respVo;
    }
}
