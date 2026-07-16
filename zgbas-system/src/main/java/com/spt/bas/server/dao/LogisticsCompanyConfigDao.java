package com.spt.bas.server.dao;

import com.spt.bas.client.entity.LogisticsCompanyConfig;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LogisticsCompanyConfigDao extends BaseDao<LogisticsCompanyConfig> {

    @Query("SELECT f FROM LogisticsCompanyConfig f WHERE f.ourCompanyNames  like CONCAT('%',?1,'%') and f.enableFlg=true ")
    List<LogisticsCompanyConfig>  findByOurCompanyNames(String  ourCompanyName);

    LogisticsCompanyConfig  findByCarrier(String carrier);


}