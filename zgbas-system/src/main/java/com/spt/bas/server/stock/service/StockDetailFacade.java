package com.spt.bas.server.stock.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spt.bas.client.entity.ApplyInternalBuyDetail;
import com.spt.bas.client.entity.CtrProduct;
import com.spt.bas.client.entity.StockContract;
import com.spt.bas.client.entity.StockDetail;
import com.spt.bas.client.vo.ApplyContractAdjustRequestVo;
import com.spt.bas.client.vo.BizUserInfor;
import com.spt.bas.client.vo.CtrConctractInvalidVo;
import com.spt.bas.client.vo.StockDetailDeleveryInResp;
import com.spt.bas.client.vo.StockDetailMoveVo;
import com.spt.bas.client.vo.StockDetailRequest;
import com.spt.tools.core.exception.ApplicationException;

@Service
public class StockDetailFacade {

	@Autowired
	private IStockDetailBuyService stockDetailBuyService;
	@Autowired
	private IStockDetailAdjustService stockDetailAdjustService;
	@Autowired
	private IStockDetailSellService stockDetailSellService;
	@Autowired
	private IStockDetailDeliveryInService stockDetailDeliveryInService;
	@Autowired
	private IStockDetailDeliveryOutService stockDetailDeliveryOutService;
	@Autowired
	private IStockDetailInternalBuyService stockDetailInternalBuyService;

	public StockDetail saveBuy(StockDetailRequest request, BizUserInfor userInfor) throws ApplicationException {
		return stockDetailBuyService.saveBuy(request, userInfor);
	}

	/** 撤销采购合同 */
	public void cancelBuyProduct(CtrProduct product) throws ApplicationException {
		stockDetailBuyService.cancelBuyProduct(product);
	}

	/** 撤销采购和入库，撮合业务下使用 */
	public void cancelDeliveryInAndBuyProduct(CtrProduct product) throws ApplicationException {
		stockDetailBuyService.cancelDeliveryInAndBuyProduct(product);
	}

	/** 作废关于该预售合同的预售采购合同 */
	public void cancelPreBuyContract(CtrProduct product, CtrConctractInvalidVo vo) throws ApplicationException {
		stockDetailBuyService.cancelPreBuyContract(product, vo);
	}
	
	/** 预售 */
	public void savePresell(CtrProduct product, BizUserInfor userInfor) throws ApplicationException{
		stockDetailSellService.savePresell(product, userInfor);
	}
	
	/** 预售作废，删除预售库存记录 */
	public void cancelPresell(CtrProduct product) throws ApplicationException {
		stockDetailSellService.cancelPresell(product);
	}

	public StockContract[] updateByContractAdjust(ApplyContractAdjustRequestVo vo)
			throws ApplicationException {
		return stockDetailAdjustService.updateByContractAdjust(vo);
	}

	public void saveSell(StockDetailRequest request) throws ApplicationException {
		stockDetailSellService.saveSell(request);
	}

	public void saveSellComplete(StockDetailRequest request) throws ApplicationException {
		stockDetailSellService.saveSellComplete(request);
	}

	/** 撤销销售合同 */
	public void cancelSellProduct(CtrProduct product) throws ApplicationException {
		stockDetailSellService.cancelSellProduct(product);
	}

	/**
	 * 入库明细保存
	 */
	public StockDetailDeleveryInResp saveDeliveryIn(StockDetailRequest request) throws ApplicationException {
		return stockDetailDeliveryInService.saveDeliveryIn(request);
	}

	public void saveDeliveryOut(StockDetailRequest request) throws ApplicationException {
		stockDetailDeliveryOutService.saveDeliveryOut(request);
	}

	public void changeWarehouse(StockDetailMoveVo changeVo) {
		stockDetailDeliveryOutService.changeWarehouse(changeVo);
	}

	public StockDetail updataByInternalBuy(ApplyInternalBuyDetail nDetail, BizUserInfor userInfo) {
		return stockDetailInternalBuyService.updataByInternalBuy(nDetail, userInfo);
	}

	public void doBackInternalBuy(StockDetail nDetail, Long oStockDetailId, Long applyId) {
		stockDetailInternalBuyService.doBackInternalBuy(nDetail, oStockDetailId, applyId);
	}
}
