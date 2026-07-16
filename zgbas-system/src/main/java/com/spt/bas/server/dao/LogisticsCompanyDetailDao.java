package com.spt.bas.server.dao;

import com.spt.bas.client.entity.LogisticsCompanyDetail;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;


public interface LogisticsCompanyDetailDao extends BaseDao<LogisticsCompanyDetail> {

    //计算承运商平均分
    @Query("SELECT round(AVG(score),1) FROM LogisticsCompanyDetail  where  logisticsCompanyId = ?1")
    BigDecimal findByCarrierScoreAVG(Long id);

    @Query("SELECT L FROM LogisticsCompanyDetail L  where  L.logisticsCompanyId = ?1 ORDER BY L.id desc")
    List<LogisticsCompanyDetail>  findByLogisticsCompanyId(Long id);

}