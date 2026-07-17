package com.spt.bas.server.service;

import com.spt.bas.client.entity.SealUsageDCSX;
import com.spt.bas.client.vo.SealUsageDcsxVo;
import com.spt.pm.vo.PmApproveSaveVo;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface ISealUsageDCSXService extends IBaseService<SealUsageDCSX> {
    void updateFileId(Long id, String fileId);

    void addSealUsageUpdateHis(PmApproveSaveVo startVo);

    void updateCfcaContractNo(@RequestBody SealUsageDcsxVo entity);

    List<SealUsageDCSX> findSealUsageDcsxByContractNo(String contractNo);
}
