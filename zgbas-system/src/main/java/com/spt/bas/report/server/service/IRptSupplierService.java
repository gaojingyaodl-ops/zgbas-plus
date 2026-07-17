package com.spt.bas.report.server.service;

import com.spt.bas.report.client.entity.RptCompany;
import com.spt.bas.report.client.entity.RptSupplier;
import com.spt.bas.report.client.vo.RptCompanySearchVo;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IRptSupplierService {
	
	List<RptSupplier> findRptSupplierList(RptCompanySearchVo vo);

	Page<RptSupplier> findRptSupplierPage(RptCompanySearchVo searchVo);

	List<RptSupplier> selectAllRptSupplier();

}
