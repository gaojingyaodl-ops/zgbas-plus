package com.spt.bas.server.service;//package com.spt.bas.server.service;
//
//import java.math.BigDecimal;
//import java.util.List;
//
//import org.springframework.data.domain.Page;
//
//import com.spt.bas.client.entity.CtrContract;
//import com.spt.bas.client.entity.CtrProduct;
//import com.spt.bas.client.entity.Stock;
//import com.spt.bas.client.entity.StockDetail;
//import com.spt.bas.client.vo.ApplyContractAdjustRequestVo;
//import com.spt.bas.client.vo.ApplySellWarehouseVo;
//import com.spt.bas.client.vo.BizUserInfor;
//import com.spt.bas.client.vo.CtrConctractInvalidVo;
//import com.spt.bas.client.vo.StockAdjustAuditVo;
//import com.spt.bas.client.vo.StockDetailLinkVo;
//import com.spt.bas.client.vo.StockDetailMoveVo;
//import com.spt.bas.client.vo.StockRequest;
//import com.spt.bas.client.vo.StockSearchVo;
//import com.spt.bas.client.vo.StockVo;
//import com.spt.tools.core.exception.ApplicationException;
//import com.spt.tools.jpa.service.IBaseService;
//
//public interface IStockService extends IBaseService<Stock> {
//
//	Stock findBrandNumber(String brandNumber, String productAttr,Long enterpriseId);
//
//	/**
//	 * 审批完成后，更新库存表的数据，库存明细数据，还有库存明细历史表数据
//	 * @param product
//	 * @param type：B-买；S-卖；
//	 */
//	void updateBuyStock(CtrProduct product,BizUserInfor userInfor,String applyType)throws ApplicationException ;
//
//	void updatePresellStock(CtrProduct product,BizUserInfor userInfor)throws ApplicationException ;
//
//	void updateSellStock(CtrProduct product,boolean isBack,StockDetailLinkVo linkVo,BizUserInfor userInfor,String applyType)throws ApplicationException ;
//
//	void updateDeliveryStock(StockRequest request,BizUserInfor userInfor) throws ApplicationException;
//
//
//	StockVo findDealNumber(ApplySellWarehouseVo vo);
//	/**
//	 * 批量入库-撮合业务审批结束后
//	 * @param productList
//	 * @param contractId
//	 */
//	void deliveryInStockByMatch(List<CtrProduct> lstProd, Long contractId,BizUserInfor userInfor) throws ApplicationException;
//	/**
//	 * 合同作废的库存相关处理
//	 * @param product
//	 */
//	void cancelContract(List<CtrContract> contractList,CtrConctractInvalidVo vo) throws ApplicationException;
//
//	List<Stock> findStockForzenNumber(String productCd, String brandNumber,Long factoryId,String warehouseName,Long enterpriseId,BigDecimal dealNumber,String productAttr);
//
//	Stock updateStock(StockDetail entity, StockDetailMoveVo changeVo);
//
//	public Page<Stock> findPageStock(StockSearchVo searchVo);
//
//	public Stock sumPageVo(StockSearchVo queryVo);
//
//	void updateByAdjust(StockAdjustAuditVo vo,StockDetail detail,String operationType);
//
//	public void cancelPreBuyContract(CtrProduct product,CtrConctractInvalidVo vo) throws ApplicationException;
//
//	public void cancelPresellProduct(CtrProduct product, CtrConctractInvalidVo vo) throws ApplicationException;
//
//	public void updateByContractAdjust(ApplyContractAdjustRequestVo vo,Long stockId);
//
//	public List<Stock> findStockByVo(ApplySellWarehouseVo vo);
//
//}
//
