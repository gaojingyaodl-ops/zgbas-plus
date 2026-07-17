package com.spt.bas.server.service;

import com.spt.bas.client.entity.BsCompanyOur;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.vo.CtrContractTextVo;
import com.spt.tools.jpa.service.IBaseService;

import java.util.List;

public interface IBsCompanyOurService extends IBaseService<BsCompanyOur> {

    BsCompanyOur findByCompanyName(String companyName);

    List<BsCompanyOur> findAllEnableOurCompany();
}
