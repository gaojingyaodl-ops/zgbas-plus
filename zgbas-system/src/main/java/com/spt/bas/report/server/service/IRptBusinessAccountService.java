package com.spt.bas.report.server.service;

import org.springframework.data.domain.Page;

import com.spt.bas.report.client.entity.RptBusinessAccountReport;
import com.spt.bas.report.client.vo.RptBusinessSearchVo;

public interface IRptBusinessAccountService {
	
	Page<RptBusinessAccountReport> findPage(RptBusinessSearchVo vo);
	
	RptBusinessAccountReport findPageSum(RptBusinessSearchVo vo);
}
