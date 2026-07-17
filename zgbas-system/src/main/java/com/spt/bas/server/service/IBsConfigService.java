package com.spt.bas.server.service;

import com.spt.bas.client.entity.ApplyMatchDetail;
import com.spt.bas.client.entity.BsConfig;
import com.spt.bas.client.vo.BsConfigReqVo;
import com.spt.bas.client.vo.BsConfigRespVo;
import com.spt.tools.jpa.service.IBaseService;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: gaojy
 * @create 2021/12/13 11:05
 * @version: 1.0
 * @description:
 */
public interface IBsConfigService extends IBaseService<BsConfig> {

    BsConfigRespVo judgmentStart(BsConfigReqVo configReqVo);

    BsConfigRespVo judgmentMatchProfit(List<ApplyMatchDetail> matchDetailList, Long enterpriseId, Boolean creditFlg);

    List<String> findConfigMessageList(Long enterpriseId);

    void refreshBalance(String approveNo,Long bsConfigId, BigDecimal applyAmount);

    /**
     * 业务查询优化，前端展示表格，findConfigMessageList 方法返回字符串不适用
     * @param enterpriseId
     * @return
     */
    List<BsConfig> getBsConfigList(Long enterpriseId);
}
