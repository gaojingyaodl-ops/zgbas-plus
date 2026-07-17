package com.spt.bas.server.service;

import com.spt.bas.client.entity.*;
import com.spt.bas.client.vo.CtrContractTextVo;
import com.spt.bas.client.vo.DcContractText;
import com.spt.bas.client.vo.MatchContractTextVo;
import com.spt.bas.client.vo.OurCompanyContractDetail;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.service.IBaseService;

import java.util.List;

public interface ICtrContractTextService extends IBaseService<CtrContractText> {

    CtrContractText saveContractText(CtrContract bc, List<CtrProduct> lstProduct) throws ApplicationException;
    CtrContractText saveContractText(CtrContract bc, List<CtrProduct> lstProduct, ApplyMatchDetail matchDetails) throws ApplicationException;

    CtrContractText saveContractText(CtrContract bc) throws ApplicationException;

    CtrContractText saveServiceText(CtrServiceContract ctrServiceContract) throws ApplicationException;

    CtrContractText findByContractIdAndContractType(Long contractId, String contractType);

    String synthesisMathContractText(MatchContractTextVo textVo) throws ApplicationException;

    OurCompanyContractDetail findOurCompanyContractDetail(String companyName, Long enterpriseId);

    CtrContractTextVo dealWithExtraBank(CtrContractTextVo textVo, ApplyMatch applyMatch, CtrContract contract, BsCompanyOur companyOur, String textKind);

    DcContractText dealWithExtraBank(DcContractText textVo,ApplyMatch applyMatch, ApplyCtrDCSX entity, String textKind);
}
