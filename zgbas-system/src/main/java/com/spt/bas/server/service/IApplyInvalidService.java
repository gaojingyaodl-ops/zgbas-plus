package com.spt.bas.server.service;

import com.spt.bas.client.entity.ApplyInvalid;
import com.spt.bas.client.vo.ApplyInvalidApproveVo;
import com.spt.bas.client.vo.ApplyInvalidDetailVo;
import com.spt.pm.entity.PmApprove;
import com.spt.tools.jpa.service.IBaseService;

import java.util.List;

/**
 * @Author MoonLight
 * @Date 2023/9/11 14:03
 * @Version 1.0
 */
public interface IApplyInvalidService extends IBaseService<ApplyInvalid> {

    void updateFileId(Long id, String fileId);

    ApplyInvalidDetailVo queryInvalidDetail(ApplyInvalid applyInvalid);

    List<ApplyInvalidApproveVo> queryInvalidApproveList(ApplyInvalid applyInvalid);
    List<ApplyInvalidApproveVo> queryInvalidApproveList(List<Long> contractIds, Long enterpriseId, Long budgetApproveId, String contractTailNo);
}
