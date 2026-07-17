package com.spt.bas.report.server.service.impl;

import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsDictData;
import com.spt.bas.report.client.entity.RptContractSettlementVo;
import com.spt.bas.report.client.vo.RptContractSettlementSearchVo;
import com.spt.bas.report.server.dao.RptCtrContractSettlementMapper;
import com.spt.bas.report.server.service.IRptContractSettlementService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class RptContractSettlementServiceImpl implements IRptContractSettlementService {
	@Autowired
	private RptCtrContractSettlementMapper contractSettlementMapper;
	
	@Override
	public Page<RptContractSettlementVo> findRptContractSettlementPage(RptContractSettlementSearchVo searchVo) {
		handelSearchParams(searchVo);
		List<RptContractSettlementVo> ctrContractSettlementList = contractSettlementMapper.findCtrContractSettlementList(searchVo);
		Pageable pageable = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows());
		Page<RptContractSettlementVo> pageVo = new PageImpl<>(ctrContractSettlementList, pageable, searchVo.getCount());
		return pageVo;
	}

	@Override
	public RptContractSettlementVo findRptContractSettlementSum(RptContractSettlementSearchVo searchVo) {
		handelSearchParams(searchVo);
		return contractSettlementMapper.findCtrContractSettlementSum(searchVo);
	}

	public void handelSearchParams(RptContractSettlementSearchVo searchVo){
		String productsName = searchVo.getProductsName();
		if( !StringUtils.isEmpty(productsName)) {
			String[] split = productsName.split("/");
			if(split.length == 1) {
				searchVo.setProductsNameOne(split[0]);
				searchVo.setLevel("1");
			} else if(split.length == 2) {
				searchVo.setProductsNameOne(split[0]);
				searchVo.setProductsNameTwo(split[1]);
				searchVo.setLevel("2");
			} else if(split.length == 3) {
				searchVo.setProductsNameOne(split[0]);
				searchVo.setProductsNameTwo(split[1]);
				searchVo.setProductsNameThree(split[2]);
				searchVo.setLevel("3");
			}
		}
		List<String> budgetTypes = searchVo.getBudgetTypes();
		if (CollectionUtils.isNotEmpty(budgetTypes)) {
			StringBuilder budgetType = new StringBuilder();
			if (budgetTypes.contains("SX")) {
				budgetType.append("SX");
			}
			if (budgetTypes.contains("DCSX")) {
				if (StringUtils.isNotBlank(budgetType)) {
					budgetType.append(",");
				}
				budgetType.append("DCSX");
			}
			if (budgetTypes.contains("DC")) {
				if (StringUtils.isNotBlank(budgetType)) {
					budgetType.append(",");
				}
				budgetType.append("DC");
			}
			searchVo.setBudgetType(budgetType.toString());

		}
	}
}
