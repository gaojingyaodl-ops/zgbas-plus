package com.spt.bas.server.service;

import com.spt.bas.client.entity.LogisticsCompanyConfig;
import com.spt.bas.client.entity.LogisticsCompanyDetail;
import com.spt.tools.jpa.service.IBaseService;

import java.math.BigDecimal;
import java.util.List;


public interface LogisticsCompanyDetailService extends IBaseService<LogisticsCompanyDetail> {

    BigDecimal findByCarrierScoreAVG(Long id);

    List<LogisticsCompanyDetail>  findByLogisticsCompanyId(Long id);

}
