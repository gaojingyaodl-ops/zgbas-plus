package com.spt.bas.report.server.service.impl;

import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.UserSearchVo;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.report.client.entity.RptCompany;
import com.spt.bas.report.client.entity.RptCompanyToProduct;
import com.spt.bas.report.client.entity.RptSupplier;
import com.spt.bas.report.client.vo.RptCompanySearchVo;
import com.spt.bas.report.server.dao.RptCompanyMapper;
import com.spt.bas.report.server.dao.RptSupplierMapper;
import com.spt.bas.report.server.service.IRptCompanyService;
import com.spt.bas.report.server.service.IRptSupplierService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class RptSupplierServiceImpl implements IRptSupplierService {
	
	@Autowired
	private RptSupplierMapper rptSupplierMapper;
	@Autowired
	private IAuthOpenFacade authOpenFacade;

	@Override
	public List<RptSupplier> findRptSupplierList(RptCompanySearchVo vo) {
		return rptSupplierMapper.findRptSupplierList(vo);
	}

    /**
     * 查询客户分析表分页数据
	 * @param searchVo
     * @return
     */
	@Override
	public Page<RptSupplier> findRptSupplierPage(RptCompanySearchVo searchVo) {

		BigDecimal grossProfitMarginStart = searchVo.getGrossProfitMarginStart();
		if(grossProfitMarginStart != null) {
			searchVo.setGrossProfitMarginStart(grossProfitMarginStart.divide(new BigDecimal("100")));
		}
		BigDecimal grossProfitMarginEnd = searchVo.getGrossProfitMarginEnd();
		if(grossProfitMarginEnd != null) {
			searchVo.setGrossProfitMarginEnd(grossProfitMarginEnd.divide(new BigDecimal("100")));
		}
		
		List<RptSupplier> rptSupplierList = rptSupplierMapper.findRptSupplierListNew(searchVo);
		
		
		Pageable pageable = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows());
		Page<RptSupplier> pageVo = new PageImpl<>(rptSupplierList, pageable, searchVo.getCount());
		return pageVo; 
	}

	@Override
	public List<RptSupplier> selectAllRptSupplier() {
		RptCompanySearchVo searchVo = new RptCompanySearchVo();
		searchVo.setRows(10000);
		
		UserSearchVo userSearchVo = new UserSearchVo(BasConstants.ZG_ENTERPRISE_ID, null);
		List<SysUserSdk> userList = authOpenFacade.findUserAll(userSearchVo);
		Map<Long,String> userNameMap = new HashMap<>();
		if(!CollectionUtils.isEmpty(userList)) {
			for (SysUserSdk sysUserSdk : userList) {
				userNameMap.put(sysUserSdk.getUserId(),sysUserSdk.getNickName());
			}
		}
		List<RptSupplier> rptSupplierList = rptSupplierMapper.findRptSupplierList(searchVo);
		List<RptCompanyToProduct> rptCompanyToProductList = rptSupplierMapper.findRptSupplierToProductList();
		Map<Long,List<RptCompanyToProduct>> companyToProductMap = new HashMap<>();
		// 处理常用牌号
		if(!CollectionUtils.isEmpty(rptCompanyToProductList)) {
			companyToProductMap = rptCompanyToProductList.stream()
					.collect(Collectors.groupingBy(RptCompanyToProduct::getCompanyId,
							Collectors.collectingAndThen(
									Collectors.toList(),
									list -> {
										list.sort(Comparator.comparingInt(RptCompanyToProduct::getTotalCount).reversed());
										return list;
									}
							)));
		}

		// 重新填充是否诉讼，常用牌号
		for (RptSupplier rptSupplier : rptSupplierList) {
			List<RptCompanyToProduct> companyToProductList = companyToProductMap.get(rptSupplier.getId());
			if(!CollectionUtils.isEmpty(companyToProductList)) {
				String commonBrandNumber = companyToProductList.stream().limit(3).map(RptCompanyToProduct::getBrandNumber).collect(Collectors.joining("，"));
				if(rptSupplier.getTradeCount() != null && !rptSupplier.getTradeCount().equals(0)) {
					rptSupplier.setCommonBrandNumber(commonBrandNumber);
				}
			}
			String userName = userNameMap.get(rptSupplier.getMatchUserId());
			if(StringUtils.isNotBlank(userName)) {
				rptSupplier.setMatchUserName(userName);
			}
		}

		return rptSupplierList;
	}
}
