package com.spt.bas.report.server.dao;

import com.spt.bas.report.client.vo.RptCtrContractUnDeliverySearchVo;
import com.spt.bas.report.client.vo.RptCtrContractUnDeliveryVo;
import com.spt.tools.mybatis.annotation.MyBatisDao;

import java.util.List;

@MyBatisDao
public interface RptCtrContractUnDeliveryMapper {
    
    List<RptCtrContractUnDeliveryVo> findUnDeliveryPage(RptCtrContractUnDeliverySearchVo vo);
}
