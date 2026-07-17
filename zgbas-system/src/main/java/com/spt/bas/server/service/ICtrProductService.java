package com.spt.bas.server.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.spt.bas.client.entity.CtrProduct;
import com.spt.bas.client.vo.CtrProductSearchVo;
import com.spt.bas.client.vo.CtrProductVo;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.jpa.service.IBaseService;

public interface ICtrProductService extends IBaseService<CtrProduct> {

	//public void updateNumberOfIn(ApplyDeliveryInVo vo);

	public List<CtrProduct> findByContractId(Long contractId);

	List<CtrProduct> findByOutCtrContractId(Long ctrContractId);

	List<CtrProduct> findEntityByParam(Map<String, Object> queryParams);
	/**查询商品详情，目前只在出库中使用*/
	Page<CtrProductVo> findProductList(CtrProductSearchVo searchVo);

	/**
	 * 确认收货商品详细
	 * @param searchVo
	 * @return
	 */
	Page<CtrProductVo> findConfirmProductList(CtrProductSearchVo searchVo);

	List<CtrProductVo> findList(PageSearchVo pageSearchVo) throws Exception;

	/**
	 * 入库时 无库存情况
	 * @param pageSearchVo
	 * @return
	 * @throws Exception
	 */
	List<CtrProductVo> findListWithNoStock(PageSearchVo pageSearchVo) throws Exception;

	public Date findMinDeliveryDateByProductId(List<Long> productList);

	public BigDecimal getNearPrice(String productCd, Long enterpriseId, String contractType);
}

