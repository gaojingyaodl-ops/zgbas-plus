package com.spt.bas.server.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.spt.bas.client.entity.BsProductType;
import com.spt.tools.jpa.dao.BaseDao;

public interface BsProductTypeDao extends BaseDao<BsProductType> {

	public List<BsProductType> findAllByOrderByIdAsc();
	
	@Query("select c from BsProductType c,BsProductTypeAccess a where c.typeCode = a.productCd and a.enterpriseId=?1 ")
	public List<BsProductType> findAllByEnterpriseId(Long enterpriseId);
	
	@Query("from BsProductType c where c.typeCode=?1 ")
	public BsProductType findProductTypeCode(String typeCode);

	@Query("from BsProductType c where c.typeName =?1  ")
	public BsProductType findHGByTypeName(String typeName);

	@Query("from BsProductType c where  c.typeCode like 'HG_%' or c.typeCode like 'AL_%' ")
	public List<BsProductType> findAllProductAlAndHg();
}

