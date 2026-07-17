package com.spt.bas.server.service;

import com.spt.bas.client.entity.ApplyMatch;
import com.spt.bas.client.entity.ApplyMatchDetail;
import com.spt.bas.client.vo.ApplyMatchVo;
import com.spt.bas.client.vo.ApproveMatchFormPrintVo;
import com.spt.pm.entity.PmApprove;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.service.IBaseService;

import java.util.List;

public interface IApplyMatchService extends IBaseService<ApplyMatch> {
    void updateFileId(Long id, String fileId);

    void updateLiabilityFileId(Long id, String fileId);

    public ApproveMatchFormPrintVo printApplyMatch(Long applyId);

    /***
     * @author shaoanwei
     * @date 2021-02-23 16:04
     * @description: 新建代采预算接口
     * @params [matchVo]
     * @return void
     */
    void applyMatch(ApplyMatchVo matchVo)throws ApplicationException ;

    /***
     * @author shaoanwei
     * @date 2021-02-25
     * @description: 新建赊销申请接口
     * @params [matchVo]
     * @return void
     */
    void applyMatchIous(ApplyMatchVo matchVo)throws ApplicationException;

    /**
     * 自动发起盖章申请
     * @param applyMatch
     * @param approve
     */
    void autoInitiatedSealUsage(ApplyMatch applyMatch, List<ApplyMatchDetail> matchDetailList, PmApprove approve);
}

