package com.spt.bas.server.service.impl;

import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.entity.DataScore;
import com.spt.bas.server.dao.BsCompanyDao;
import com.spt.bas.server.dao.DataScoreDao;
import com.spt.bas.server.service.IDataScoreService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2021-01-20 09:49
 */
@Component
public class DataScoreServiceImpl extends BaseService<DataScore> implements IDataScoreService {
    @Autowired
    private DataScoreDao dataScoreDao;
    @Autowired
    private BsCompanyDao companyDao;

    @Override
    public BaseDao<DataScore> getBaseDao() {
        return dataScoreDao;
    }

    /**
     * 获取企业最新评分
     *
     * @param companyId
     * @return
     */
    @Override
    public BigDecimal getCompanyDataScore(Long companyId) {
        BigDecimal r = BigDecimal.ZERO;
        List<DataScore> dataScores = dataScoreDao.findByCompanyIdOrderByIdDesc(companyId);
        if (!dataScores.isEmpty()) {
            DataScore dataScore = dataScores.get(0);
            if (dataScore.getInvestigateScore() != null) {
                r = r.add(dataScore.getInvestigateScore());
            }
            if (dataScore.getRiskScore() != null) {
                r = r.add(dataScore.getRiskScore());
            }
            if (dataScore.getActiveTradeScore() != null) {
                r = r.add(dataScore.getActiveTradeScore());
            }
            if (dataScore.getBasicAbilityScore() != null) {
                r = r.add(dataScore.getBasicAbilityScore());
            }
            if (dataScore.getHistoryTradeScore() != null) {
                r = r.add(dataScore.getHistoryTradeScore());
            }
            if (dataScore.getIncreaseScore() != null) {
                r = r.add(dataScore.getIncreaseScore());
            }
        }
        return r;
    }

    /**
     * @param companyId
     * @return
     */
    @Override
    public BigDecimal getLatestBaseAmount(Long companyId) {
        return BigDecimal.ZERO;
    }
}
