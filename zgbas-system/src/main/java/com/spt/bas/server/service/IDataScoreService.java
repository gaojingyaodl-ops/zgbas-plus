package com.spt.bas.server.service;

import com.spt.bas.client.entity.DataScore;
import com.spt.tools.jpa.service.IBaseService;

import java.math.BigDecimal;

public interface IDataScoreService extends IBaseService<DataScore> {
    /**
     * 获取企业最新评分
     * @param companyId
     * @return
     */
    BigDecimal getCompanyDataScore(Long companyId);

    /**
     *
     * @param companyId
     * @return
     */
    BigDecimal getLatestBaseAmount(Long companyId);

}
