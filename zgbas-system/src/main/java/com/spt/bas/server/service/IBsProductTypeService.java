package com.spt.bas.server.service;

import com.spt.bas.client.entity.BsProductType;
import com.spt.tools.jpa.service.IBaseService;

import java.util.List;

public interface IBsProductTypeService extends IBaseService<BsProductType> {

	List<BsProductType> findByList(String typeCode);

	BsProductType findProductTypeCode(String typeCode);

	List<BsProductType> findAllByEnterpriseId(Long enterpriseId);

	BsProductType findHGByTypeName(String typeName);

	List<BsProductType> findAllProductAlAndHg();
}

