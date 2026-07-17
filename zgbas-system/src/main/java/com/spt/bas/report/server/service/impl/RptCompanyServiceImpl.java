package com.spt.bas.report.server.service.impl;

import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.UserSearchVo;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.report.client.entity.RptCompany;
import com.spt.bas.report.client.entity.RptCompanyToProduct;
import com.spt.bas.report.client.vo.RptCompanySearchVo;
import com.spt.bas.report.server.dao.RptCompanyMapper;
import com.spt.bas.report.server.service.IRptCompanyService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class RptCompanyServiceImpl implements IRptCompanyService {
	
	@Autowired
	private RptCompanyMapper rptCompanyMapper;
	@Autowired
	private IAuthOpenFacade authOpenFacade;
	

	@Override
	public List<RptCompany> findRptCompanyList(RptCompanySearchVo vo) {
		return rptCompanyMapper.findRptCompanyList(vo);
	}

	/**
	 * 查询客户分析表分页数据
	 * @param searchVo
	 * @return
	 */
	@Override
	public Page<RptCompany> findRptCompanyPage(RptCompanySearchVo searchVo) {

		BigDecimal grossProfitMarginStart = searchVo.getGrossProfitMarginStart();
		if(grossProfitMarginStart != null) {
			searchVo.setGrossProfitMarginStart(grossProfitMarginStart.divide(new BigDecimal("100")));
		}
		BigDecimal grossProfitMarginEnd = searchVo.getGrossProfitMarginEnd();
		if(grossProfitMarginEnd != null) {
			searchVo.setGrossProfitMarginEnd(grossProfitMarginEnd.divide(new BigDecimal("100")));
		}
		
		List<RptCompany> rptCompanyList = rptCompanyMapper.findRptCompanyListNew(searchVo);
		UserSearchVo userSearchVo = new UserSearchVo(BasConstants.ZG_ENTERPRISE_ID, null);
		List<SysUserSdk> userList = authOpenFacade.findUserAll(userSearchVo);
		Map<Long,String> userNameMap = new HashMap<>();
		if(!CollectionUtils.isEmpty(userList)) {
			for (SysUserSdk sysUserSdk : userList) {
				userNameMap.put(sysUserSdk.getUserId(),sysUserSdk.getNickName());
			}
		}
		if(CollectionUtils.isNotEmpty(rptCompanyList)) {
			for (RptCompany rptCompany : rptCompanyList) {
				String ownerOfAccountName = userNameMap.get(rptCompany.getOwnerOfAccountId());
				if (StringUtils.isNotBlank(ownerOfAccountName)) {
					rptCompany.setOwnerOfAccountName(ownerOfAccountName);
				}
			}
		}


		Pageable pageable = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows());
		Page<RptCompany> pageVo = new PageImpl<>(rptCompanyList, pageable, searchVo.getCount());
		return pageVo; 
	}

	@Override
	public List<RptCompany> selectAllRptCompany() {
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
		List<RptCompany> rptCompanyList = rptCompanyMapper.findRptCompanyList(searchVo);
		List<RptCompanyToProduct> rptCompanyToProductList = rptCompanyMapper.findRptCompanyToProductList();
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
		// 是否诉讼
		List<RptCompanyToProduct> rptLegalList = rptCompanyMapper.findRptLegalList();
		Map<Long, List<RptCompanyToProduct>> legalMap = new HashMap<>();
		if(!CollectionUtils.isEmpty(rptLegalList)) {
			legalMap = rptLegalList.stream().collect(Collectors.groupingBy(RptCompanyToProduct::getCompanyId));
		}

		// 重新填充是否诉讼，常用牌号
		for (RptCompany rptCompany : rptCompanyList) {
			List<RptCompanyToProduct> companyToProductList = companyToProductMap.get(rptCompany.getId());
			if(!CollectionUtils.isEmpty(companyToProductList)) {
				String commonBrandNumber = companyToProductList.stream().limit(3).map(RptCompanyToProduct::getBrandNumber).collect(Collectors.joining("，"));
				if(rptCompany.getTradeCount() != null && !rptCompany.getTradeCount().equals(0)) {
					rptCompany.setCommonBrandNumber(commonBrandNumber);
				}
			}
			List<RptCompanyToProduct> legalList = legalMap.get(rptCompany.getId());
			if(!CollectionUtils.isEmpty(legalList)){
				rptCompany.setLegalFlg(true);
			}
			String userName = userNameMap.get(rptCompany.getMatchUserId());
			if(StringUtils.isNotBlank(userName)) {
				rptCompany.setMatchUserName(userName);
			}
			String ownerOfAccountName = userNameMap.get(rptCompany.getOwnerOfAccountId());
			if (StringUtils.isNotBlank(ownerOfAccountName)) {
				rptCompany.setOwnerOfAccountName(ownerOfAccountName);
			}

		}

		return rptCompanyList;
		
	}


}
