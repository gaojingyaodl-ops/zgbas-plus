package com.spt.bas.server.service;


import com.spt.bas.client.entity.ApplyCtrDCSX;
import com.spt.bas.client.entity.ApplyMatch;
import com.spt.bas.client.entity.ApplyMatchDetail;
import com.spt.bas.client.entity.BsCompanyDcsx;
import com.spt.bas.client.vo.BsBankVo;
import com.spt.pm.entity.PmApprove;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.service.IBaseService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ApplyChargeSalesService extends IBaseService<ApplyMatch> {


    /**
     * 自动发起盖章申请
     * @param applyCtrDcsx
     * @param applyMatch
     * @param matchDetailList
     * @param approve
     */
    void autoInitiatedSealUsage(ApplyCtrDCSX applyCtrDcsx, ApplyMatch applyMatch, List<ApplyMatchDetail> matchDetailList, PmApprove approve);

    /**
     * 生成中间链条合同
     * @param applyMatch
     * @param matchDetailList
     * @return
     */
    void generateChainContract(ApplyMatch applyMatch, ApplyCtrDCSX applyCtrDCSX, List<ApplyMatchDetail> matchDetailList);


    BsBankVo getSpecialBank(Long enterpriseId);

    ApplyCtrDCSX parseCtrDcsx(PmApprove approve, ApplyMatch match, ApplyMatchDetail buyDetail, ApplyMatchDetail sellDetail);

    ApplyCtrDCSX parseCtrDcsxByApproveId(Long approveId);

    BigDecimal calculatePrice(ApplyMatch applyMatch, Long creditDays, Map<String, BsCompanyDcsx> companyConfigMap);
}
