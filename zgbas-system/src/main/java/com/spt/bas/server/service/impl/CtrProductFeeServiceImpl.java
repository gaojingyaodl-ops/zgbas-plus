package com.spt.bas.server.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyDelivery;
import com.spt.bas.client.entity.ApplyDeliveryOut;
import com.spt.bas.client.entity.ApplyProductDetail;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.entity.CtrContractRela;
import com.spt.bas.client.entity.CtrProduct;
import com.spt.bas.client.entity.CtrProductFee;
import com.spt.bas.client.entity.StockContract;
import com.spt.bas.client.vo.ApplyDeliveryReportVo;
import com.spt.bas.client.vo.CtrProductFeeVo;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.CtrContractRelaDao;
import com.spt.bas.server.dao.CtrProductFeeDao;
import com.spt.bas.server.service.IApplyDeliveryOutService;
import com.spt.bas.server.service.IApplyProductDetailService;
import com.spt.bas.server.service.ICtrContractService;
import com.spt.bas.server.service.ICtrProductFeeService;
import com.spt.bas.server.service.ICtrProductService;
import com.spt.bas.server.stock.service.IStockContractService;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;

@Component
@Transactional(readOnly = true)
public class CtrProductFeeServiceImpl extends BaseService<CtrProductFee> implements ICtrProductFeeService {
	@Autowired
	private CtrProductFeeDao ctrProductFeeDao;
	@Autowired
	private IStockContractService stockContractService;
	@Autowired
	private ICtrContractService ctrContractService;
	@Autowired
	private ICtrProductService ctrProductService;
	@Autowired
	private IApplyDeliveryOutService applyDeliveryOutService;
	@Autowired
	private IApplyProductDetailService applyProductDetailService;
	@Autowired
	private CtrContractRelaDao ctrContractRelaDao;
	
	@Override
	public BaseDao<CtrProductFee> getBaseDao() {
		return ctrProductFeeDao;
	}
	
	@Override
	public Class<CtrProductFee> getEntityClazz() {
		return CtrProductFee.class;
	}
	
	/**
	 * 根据提货单生成商品费用记录
	 * @throws ApplicationException 
	 */
	@Override
	@ServerTransactional
	public void saveCtrProductFee(ApplyDelivery applyDelivery) throws ApplicationException {
		CtrProductFee fee = new CtrProductFee();
		Long contractId = applyDelivery.getContractId();
		Long productId = applyDelivery.getProductId();
		CtrContract contract = ctrContractService.getEntity(contractId);
		CtrProduct product = ctrProductService.getEntity(productId);
		Long stockContractId = product.getStockContractId();
		fee.setId(0L);
		fee.setContractId(contractId);
		fee.setProductId(productId);
		fee.setApplyDeliveryId(applyDelivery.getId());
		fee.setEnterpriseId(contract.getEnterpriseId());
		fee.setCcUserXs(contract.getMatchUserName());
		StockContract stockContract = stockContractService.getEntity(stockContractId);
		if (stockContract != null) {
			Long buyContractId = stockContract.getBuyContractId();
			CtrContract buyContract = ctrContractService.getEntity(buyContractId);
			String matchUserName = buyContract.getMatchUserName();
			fee.setCcUserCg(matchUserName);
			fee.setCcUserQt(matchUserName);
			fee.setCcUserRuku(matchUserName);
			fee.setWlUserDfcc(matchUserName);
			fee.setWlUserQt(matchUserName);
			fee.setWlUserYs(matchUserName);
			this.save(fee);
		}
	}

	@Override
	public CtrProductFee findByDeliveryId(CtrProductFeeVo vo) {
		CtrProductFee fee = ctrProductFeeDao.findByApplyDeliveryIdAndEnterpriseId(vo.getApplyDeliveryId(), vo.getEnterpriseId());
		return fee;
	}

	@Override
	@ServerTransactional
	public void saveProductFee(ApplyDeliveryReportVo delivery) throws ApplicationException {
		Long applyDeliveryId = delivery.getApplyDeliveryId();
		Long enterpriseId = delivery.getEnterpriseId();
		CtrProductFee fee = ctrProductFeeDao.findByApplyDeliveryIdAndEnterpriseId(applyDeliveryId, enterpriseId);
		if (fee == null) {
			fee = new CtrProductFee();
			fee.setContractId(delivery.getContractId());
			fee.setProductId(delivery.getProductId());
			fee.setApplyDeliveryId(applyDeliveryId);
			fee.setEnterpriseId(enterpriseId);
		}
		fee.setCcFeeCg(delivery.getCcFeeCg());
		fee.setCcFeeQt(delivery.getCcFeeQt());
		fee.setCcFeeRate(delivery.getCcFeeRate());
		fee.setCcFeeRuku(delivery.getCcFeeRuku());
		fee.setCcFeeXs(delivery.getCcFeeXs());
		fee.setCcUserCg(delivery.getCcUserCg());
		fee.setCcUserQt(delivery.getCcUserQt());
		fee.setCcUserRuku(delivery.getCcUserRuku());
		fee.setCcUserXs(delivery.getCcUserXs());
		fee.setWlFeeDfcc(delivery.getWlFeeDfcc());
		fee.setWlFeeQt(delivery.getWlFeeQt());
		fee.setWlFeeRate(delivery.getWlFeeRate());
		fee.setWlFeeYs(delivery.getWlFeeYs());
		fee.setWlFeeZc(delivery.getWlFeeZc());
		fee.setWlUserDfcc(delivery.getWlUserDfcc());
		fee.setWlUserQt(delivery.getWlUserQt());
		fee.setWlUserYs(delivery.getWlUserYs());
		CtrProductFee productFee = this.save(fee);
		//更新合同实际运输费,实际仓储费
		saveContractRealAmount(productFee.getContractId());
	}
	
	@Override
	@ServerTransactional
	public void saveContractRealAmount(Long contractId) throws ApplicationException {
		List<CtrProductFee> feeList = ctrProductFeeDao.findByContractId(contractId);
		if (!feeList.isEmpty()) {
			BigDecimal realTransportAmount = BigDecimal.ZERO;
			BigDecimal realWarehouseAmount = BigDecimal.ZERO;
			for (CtrProductFee ctrProductFee : feeList) {
				BigDecimal wlFeeYs = ctrProductFee.getWlFeeYs() == null ? BigDecimal.ZERO : ctrProductFee.getWlFeeYs();
				BigDecimal wlFeeZc = ctrProductFee.getWlFeeZc() == null ? BigDecimal.ZERO : ctrProductFee.getWlFeeZc();
				BigDecimal wlFeeDfcc = ctrProductFee.getWlFeeDfcc() == null ? BigDecimal.ZERO : ctrProductFee.getWlFeeDfcc();
				BigDecimal wlFeeQt = ctrProductFee.getWlFeeQt() == null ? BigDecimal.ZERO : ctrProductFee.getWlFeeQt();
				BigDecimal ccFeeXs = ctrProductFee.getCcFeeXs() == null ? BigDecimal.ZERO : ctrProductFee.getCcFeeXs();
				BigDecimal ccFeeCg = ctrProductFee.getCcFeeCg() == null ? BigDecimal.ZERO : ctrProductFee.getCcFeeCg();
				BigDecimal ccFeeRuku = ctrProductFee.getCcFeeRuku() == null ? BigDecimal.ZERO : ctrProductFee.getCcFeeRuku();
				BigDecimal ccFeeQt = ctrProductFee.getCcFeeQt() == null ? BigDecimal.ZERO : ctrProductFee.getCcFeeQt();
				realTransportAmount = realTransportAmount.add(wlFeeYs).add(wlFeeZc).add(wlFeeDfcc).add(wlFeeQt);
				realWarehouseAmount = realWarehouseAmount.add(ccFeeXs).add(ccFeeCg).add(ccFeeRuku).add(ccFeeQt);
			}
			CtrContract contract = ctrContractService.getEntity(contractId);
			if (contract != null) {
				contract.setRealTransportAmount(realTransportAmount);
				contract.setRealWarehouseAmount(realWarehouseAmount);
				ctrContractService.save(contract);
			}
		}
	}

	@Override
	public CtrProductFee getDefaultCtrProductFee(Long deliveryOutId) {
		CtrProductFee fee = new CtrProductFee();
		ApplyDeliveryOut deliveryOut = applyDeliveryOutService.getEntity(deliveryOutId);
		List<ApplyProductDetail> apdList = applyProductDetailService.findApplyDetail(deliveryOutId, BasConstants.APPLY_TYPE_O);
		if (!apdList.isEmpty()) {
			ApplyProductDetail productDetail = apdList.get(0);
			Long ctrProductId = productDetail.getCtrProductId();
			List<CtrContractRela> relaList = ctrContractRelaDao.findBySellProductIdAndSellContractId(ctrProductId,
					deliveryOut.getContractId());
			if (!relaList.isEmpty()) {
				CtrContractRela ctrContractRela = relaList.get(0);
				String buyUserName = ctrContractRela.getBuyUserName();
				String sellUserName = ctrContractRela.getSellUserName();
				fee.setCcUserXs(sellUserName);
				fee.setCcUserCg(buyUserName);
				fee.setCcUserRuku(buyUserName);
				fee.setCcUserQt(buyUserName);
				fee.setWlUserDfcc(buyUserName);
				fee.setWlUserQt(buyUserName);
				fee.setWlUserYs(buyUserName);
			}
		}
		return fee;
	}
	
}

