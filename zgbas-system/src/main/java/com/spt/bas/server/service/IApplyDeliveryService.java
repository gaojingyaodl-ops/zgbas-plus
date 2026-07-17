package com.spt.bas.server.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.spt.bas.client.entity.ApplyDelivery;
import com.spt.bas.client.entity.ApplyDeliveryOut;
import com.spt.bas.client.entity.ApplyProductDetail;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.vo.ApplyDeliveryApplyIdVo;
import com.spt.bas.client.vo.ApplyDeliveryCancelVo;
import com.spt.bas.client.vo.ApplyDeliveryReportVo;
import com.spt.bas.client.vo.ApplyDeliveryVo;
import com.spt.bas.client.vo.DeliveryDetailVo;
import com.spt.pm.entity.PmApprove;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.service.IBaseService;

public interface IApplyDeliveryService extends IBaseService<ApplyDelivery> {

	void startPrint(ApplyDeliveryVo vo);

	void insertDelivery(PmApprove approve, ApplyDeliveryOut out, String entityName) throws ApplicationException;

	void insertDeliveryByMatch(PmApprove approve, CtrContract ctr, List<ApplyProductDetail> list)
			throws ApplicationException;

	/*提货单*/
	ApplyDelivery getApplyDeliveryEntity(ApplyDeliveryApplyIdVo vo);

	/*转货权单*/
	ApplyDelivery getApplyDeliveryInvoiceEntity(ApplyDeliveryApplyIdVo vo);

	/*中信配送单*/
	ApplyDelivery getApplyDeliveryDistributionEntity(ApplyDeliveryApplyIdVo vo);

	ApplyDelivery getApplyDeliverySendSingleEntity(ApplyDeliveryApplyIdVo vo);

	/** 提货单作废 */
	void doCancel(ApplyDeliveryCancelVo cancelVo) throws ApplicationException;

	List<ApplyDelivery> findByContractId(Long id);

	public Page<DeliveryDetailVo> findPageDetail(PageSearchVo searchVo);

	void saveDelivery(ApplyProductDetail d, PmApprove approve, ApplyDeliveryOut out) throws ApplicationException;

	void saveDetail(ApplyDeliveryReportVo delivery) throws ApplicationException;
}
