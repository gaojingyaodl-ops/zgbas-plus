package com.spt.bas.report.server.dao;

import java.util.List;

import com.spt.bas.report.client.entity.RptApplyDeliveryReport;
import com.spt.bas.report.client.vo.RptApplyDeliverySearchVo;
import com.spt.tools.mybatis.annotation.MyBatisDao;

@MyBatisDao
public interface RptApplyDeliveryMapper {
	/* 出库表单查询 */
	List<RptApplyDeliveryReport> findApplyDeliveryPage(RptApplyDeliverySearchVo searchVo);
}
