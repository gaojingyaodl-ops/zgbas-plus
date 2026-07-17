package com.spt.bas.server.stock.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.entity.StockDetail;
import com.spt.bas.server.dao.CtrContractDao;
import com.spt.bas.server.service.IStockDetailService;
import com.spt.bas.server.stock.service.IStockDetailTransferService;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.core.exception.ApplicationException;

/**
 * 库存明细数据迁移
 * 
 * @author wlddh
 *
 */
@Component
public class StockDataTransferService {
	private static final Logger log = LoggerFactory.getLogger(StockDataTransferService.class);
	@Autowired
	private IStockDetailTransferService stockDetailTransferService;
	@Autowired
	private IStockDetailService stockDetailService;
	@Autowired
	private CtrContractDao contractDao;

	public void transfer(Long enterpriseId) {
		log.info(">>> transfer start <<<");
		int pageNo = 0;
		int pageSize = 100;
		PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
		Map<String, Object> searchParams = new HashMap<>();
		searchParams.put("EQL_enterpriseId", enterpriseId);
		searchParams.put("ISNULLL_stockContractId", 0L);// is null
		searchParams.put("GTM_availableNumber_OR_GTM_frozenNumber_OR_GTM_deliveryOutNumber", 0L);
		Page<StockDetail> page = stockDetailService.findPage(searchParams, pageRequest);
		while (page != null && page.hasContent()) {

			page.getContent().forEach(d -> {
				// 循环处理库存明细
				try {
					stockDetailTransferService.refreshRela(d);
				} catch (ApplicationException e) {
					log.warn("tranStockDetail error", e);
				}
			});

			if (page.hasNext()) {
				page = stockDetailService.findPage(searchParams, pageRequest);
			} else {
				page = null;
			}

		}
		log.info(">>> transfer end <<<");
	}

	/**
	 * 重新生成合同库存关联数据
	 * 
	 * @throws ApplicationException
	 */
	public void transfer(String buyContractNo) throws ApplicationException {
		CtrContract ctr = contractDao.findByContractNo(buyContractNo);
		PageRequest pageRequest = PageRequest.of(0, 1000);
		Map<String, Object> searchParams = new HashMap<>();
//		searchParams.put("EQL_enterpriseId", 2L);
		searchParams.put("EQS_buyContractId", String.valueOf(ctr.getId()));
		searchParams.put("LTD_createdDate", DateOperator.parse("2019-10-11"));
		searchParams.put("GTM_availableNumber_OR_GTM_frozenNumber_OR_GTM_deliveryOutNumber", 0L);
		Page<StockDetail> page = stockDetailService.findPage(searchParams, pageRequest);
		stockDetailTransferService.cleanByContractId(ctr.getId());
		if (BasConstants.CONTRACTSTATUS_C.equals(ctr.getContractStatus())) {
			// 作废合同不生成库存记录
			return;
		}
		page.getContent().forEach(d -> {
			// 循环处理库存明细
			try {
//				stockDetailTransferService.clean(d);
				stockDetailTransferService.refreshRela(d);
			} catch (ApplicationException e) {
				log.warn("clean & tranStockDetail error", e);
			}
		});

	}

	/** 刷新合同库存关联表 */
	public void refreshRela(Long enterpriseId) {
		int pageNo = 0;
		PageRequest pageRequest = PageRequest.of(pageNo, 1000);
		Map<String, Object> searchParams = new HashMap<>();
		searchParams.put("EQL_enterpriseId", enterpriseId);
		searchParams.put("NNS_sellContractCfs", "1");// is not null
		searchParams.put("GTM_availableNumber_OR_GTM_frozenNumber_OR_GTM_deliveryOutNumber", 0L);
		Page<StockDetail> page = stockDetailService.findPage(searchParams, pageRequest);
		while (page != null && page.hasContent()) {
			page.getContent().forEach(d -> {
				// 循环处理库存明细
				try {
					stockDetailTransferService.refreshRela(d);
				} catch (ApplicationException e) {
					log.warn("clean & tranStockDetail error", e);
				}
			});
			if (page.hasNext()) {
				pageRequest = (PageRequest) pageRequest.next();
				page = stockDetailService.findPage(searchParams, pageRequest);
			} else {
				page = null;
			}
		}
	}

}
