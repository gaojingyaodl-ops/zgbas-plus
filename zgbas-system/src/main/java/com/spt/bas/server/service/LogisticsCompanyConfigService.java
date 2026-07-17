package com.spt.bas.server.service;


import com.spt.bas.client.entity.LogisticsCompanyConfig;
import com.spt.tools.jpa.service.IBaseService;

import java.util.List;


public interface LogisticsCompanyConfigService extends IBaseService<LogisticsCompanyConfig> {
    /**
     * 根据承运商查询物流配置信息
     * @param
     * @return
     */
    LogisticsCompanyConfig getByCarrier(LogisticsCompanyConfig logisticsCompanyConfig);

    /**
     * 我方企业查寻数据
     * @param ourCompanyName
     * @return
     */
    List<LogisticsCompanyConfig> findByOurCompanyNames(String  ourCompanyName);


    LogisticsCompanyConfig  findByCarrier(String carrier);

}
