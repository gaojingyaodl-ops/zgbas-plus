package com.spt.bas.server.stock.service;

import java.util.List;

import com.spt.bas.client.entity.CtrProduct;
import com.spt.bas.client.entity.StockContract;
import com.spt.bas.client.vo.ApplyContractAdjustRequestVo;
import com.spt.bas.client.vo.StockDetailRequest;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.service.IBaseService;

public interface IStockContractService extends IBaseService<StockContract> {
	/** 采购时，生成合同库存记录 */
	StockContract saveBuy(StockDetailRequest request);

	/** 作废采购时，删除合同库存记录 */
	void cancelBuy(CtrProduct product);

	/** 销售审批完成时，扣减销售中数量，增加已销售数量，并增加销售关联记录 */
	void saveSellComplete(StockDetailRequest request) throws ApplicationException;

	/** 发起销售申请时，增加销售中数量 */
	void saveSell(StockDetailRequest request) throws ApplicationException;

	/** 销售驳回，扣减销售中数量 */
	void saveSellBack(StockDetailRequest request) throws ApplicationException;

	/** 销售作废，扣减已销售数量，并删除销售关联记录 */
	void cancelSell(CtrProduct product) throws ApplicationException;

	/** 入库作废，扣减已入库数量，并删除入库关联记录 */
	void cancelDeliveryIn(StockDetailRequest request) throws ApplicationException;

	/** 入库审批完成，增加已入库数量，并增加入库关联记录 */
	void saveDeliveryIn(StockDetailRequest request) throws ApplicationException;

	/** 出库作废，扣减已出库数量，并删除出库关联记录 */
	void cancelDeliveryOut(StockDetailRequest request) throws ApplicationException;

	/** 出库审批完成，增加已出库数量，并增加出库关联记录 */
	void saveDeliveryOut(StockDetailRequest request) throws ApplicationException;

	/** 合同调整，采购合同调整：修改采购数量；销售合同调整：修改销售数量，并修改销售关联记录的数量 */
	StockContract[] saveAdjust(ApplyContractAdjustRequestVo request) throws ApplicationException;

	/** 自定义查询合同库存 */
	//Page<StockContractVo> findPageStockContractList(StockContractSearchVo queryVo);
	
	/** 销售选择合同库存(关联合同) */
	//Page<StockContractVo> findPageVo(StockContractSearchVo queryVo);
	
	/** 根据buyContractId获取合同库存*/
	List<StockContract> findByBuyContractId(Long buyContractId);

}
