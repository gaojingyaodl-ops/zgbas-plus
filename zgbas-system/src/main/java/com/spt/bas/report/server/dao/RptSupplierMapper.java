package com.spt.bas.report.server.dao;

import com.spt.bas.report.client.entity.RptCompany;
import com.spt.bas.report.client.entity.RptCompanyToProduct;
import com.spt.bas.report.client.entity.RptSupplier;
import com.spt.bas.report.client.vo.RptCompanySearchVo;
import com.spt.tools.mybatis.annotation.MyBatisDao;

import java.util.List;

@MyBatisDao
public interface RptSupplierMapper {
	
	List<RptSupplier> findRptSupplierList(RptCompanySearchVo vo);
	
	List<RptSupplier> findRptSupplierListNew(RptCompanySearchVo vo);
	
	List<RptCompanyToProduct> findRptSupplierToProductList();
	

}
