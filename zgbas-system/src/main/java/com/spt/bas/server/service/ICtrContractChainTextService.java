package com.spt.bas.server.service;

import com.spt.bas.client.entity.*;
import com.spt.bas.client.vo.MatchContractTextVo;
import com.spt.bas.client.vo.OurCompanyContractDetail;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.service.IBaseService;

import java.util.List;

public interface ICtrContractChainTextService extends IBaseService<CtrContractChainText> {

    CtrContractChainText saveContractText(CtrContract bc, List<CtrProduct> lstProduct) throws ApplicationException;

    CtrContractChainText saveContractText(CtrContract bc) throws ApplicationException;

    CtrContractChainText saveServiceText(CtrServiceContract ctrServiceContract) throws ApplicationException;

    CtrContractChainText findByContractIdAndContractType(Long contractId, String contractType);
}
