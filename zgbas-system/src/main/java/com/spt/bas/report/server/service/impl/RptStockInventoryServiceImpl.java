package com.spt.bas.report.server.service.impl;

import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.DeptSearchVo;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsCompanyDcsx;
import com.spt.bas.client.entity.BsFunder;
import com.spt.bas.report.client.entity.*;
import com.spt.bas.report.client.vo.RptStockBookVo;
import com.spt.bas.report.client.vo.RptWarehouseOutSearchVo;
import com.spt.bas.report.server.dao.RptStockInventoryMapper;
import com.spt.bas.report.server.service.IRptStockInventoryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class RptStockInventoryServiceImpl implements IRptStockInventoryService {
	@Autowired
	private RptStockInventoryMapper stockInventoryMapper;
	@Autowired
	private IAuthOpenFacade authOpenFacade;
	@Override
	public Page<RptStockInventoryReport> findPageStockInventory(RptStockInventoryReport vo) {
		List<RptStockInventoryReport> list = stockInventoryMapper.findPageStockInventory(vo);
		List<RptStockInventoryReport> listVo = new ArrayList<>();
		for (RptStockInventoryReport stockInventoryReport : list) {
			RptStockInventoryReport report = new RptStockInventoryReport();
			BeanUtils.copyProperties(stockInventoryReport, report);
			//查询部门负责人
			DeptSearchVo sysDeptSearchVo = new DeptSearchVo();
			sysDeptSearchVo.setEnterpriseId(vo.getEnterpriseId());
			sysDeptSearchVo.setUserId(stockInventoryReport.getMatchUserId());
			Long findDeptLeader = authOpenFacade.findDeptLeader(sysDeptSearchVo);
			if(findDeptLeader !=null){
				SysUserSdk sysUser = authOpenFacade.findUserById(findDeptLeader);
				report.setTheirTeam(sysUser.getNickName());
			}
			listVo.add(report);
		}
		Pageable pageable = PageRequest.of(vo.getPage() - 1, vo.getRows());
		Page<RptStockInventoryReport> pageVo = new PageImpl<>(listVo, pageable, vo.getCount());
		return pageVo;
	}
	/**
	 * 库存统计合计
	 */
	@Override
	public RptStockInventoryReport findTotalStockInventory(RptStockInventoryReport vo) {
		vo.setCount(-1);
		RptStockInventoryReport total = stockInventoryMapper.findTotalStockInventory(vo);
		if(total==null){
			total = new RptStockInventoryReport();
		}
		return total;
	}
	@Override
	public Page<RptDeliveryOutReport> findDeliveryOut(RptDeliveryOutReport vo) {
		List<RptDeliveryOutReport> list = stockInventoryMapper.findDeliveryOut(vo);
		Pageable pageable = PageRequest.of(vo.getPage() - 1, vo.getRows());
		Page<RptDeliveryOutReport> pageVo = new PageImpl<>(list, pageable, vo.getCount());
		return pageVo;
	}
	@Override
	public RptDeliveryOutReport findTotalDeliveryOut(RptDeliveryOutReport vo) {
		vo.setCount(-1);
		RptDeliveryOutReport total = stockInventoryMapper.findTotalDeliveryOut(vo);
		if(total==null){
			total = new RptDeliveryOutReport();
		}
		return total;
	}
	@Override
	public Page<RptStockDetailReport> findStockDetailPage(RptStockDetailReport vo) {
		List<RptStockDetailReport> list = stockInventoryMapper.findStockDetailPage(vo);
		Pageable pageable = PageRequest.of(vo.getPage() - 1, vo.getRows());
		Page<RptStockDetailReport> pageVo = new PageImpl<>(list, pageable, vo.getCount());
		return pageVo;
	}
	@Override
	public RptStockDetailReport findStockDetailTotal(RptStockDetailReport vo) {
		vo.setCount(-1);
		RptStockDetailReport total = stockInventoryMapper.findStockDetailTotal(vo);
		if(total==null){
			total = new RptStockDetailReport();
		}
		return total;
	}
	@Override
	public Page<RptStockReport> findStockPage(RptStockReport vo) {
		List<RptStockReport> list = stockInventoryMapper.findStockPage(vo);
		Pageable pageable = PageRequest.of(vo.getPage() - 1, vo.getRows());
		Page<RptStockReport> pageVo = new PageImpl<>(list, pageable, vo.getCount());
		return pageVo;
	}
	@Override
	public RptStockReport findStockPageTotal(RptStockReport vo) {
		vo.setCount(-1);
		RptStockReport total = stockInventoryMapper.findStockPageTotal(vo);
		if(total==null){
			total = new RptStockReport();
		}
		return total;
	}

	@Override
	public Page<RptWarehouseOutEntity> findWarehouseOut(RptWarehouseOutSearchVo vo) {
		List<RptWarehouseOutEntity> list = stockInventoryMapper.findWarehouseOut(vo);
		Pageable pageable = PageRequest.of(vo.getPage() - 1, vo.getRows());
		Page<RptWarehouseOutEntity> page = new PageImpl<>(list, pageable, vo.getCount());
		return page;
	}

	@Override
	public Page<RptStockDetailReport> findRealStockDetailPage(RptStockDetailReport vo) {
		List<RptStockDetailReport> list = stockInventoryMapper.findRealStockDetailPage(vo);
		Pageable pageable = PageRequest.of(vo.getPage() - 1, vo.getRows());
		Page<RptStockDetailReport> pageVo = new PageImpl<>(list, pageable, vo.getCount());
		return pageVo;
	}

	@Override
	public RptStockDetailReport findRealStockDetailTotal(RptStockDetailReport vo) {
		vo.setCount(-1);
		RptStockDetailReport total = stockInventoryMapper.findRealStockDetailTotal(vo);
		if(total==null){
			total = new RptStockDetailReport();
		}
		return total;
	}

	@Override
	public Page<RptStockBook> findPageStockBook(RptStockBookVo vo) {
		if(!vo.getViewAllFlg()){
			// 资金方账号只能看本企业的数据，资金方能查看的企业在“资金方管理”中查看
			List<BsFunder>bsFunderList = stockInventoryMapper.finAllBsFunder();
			List<String> ourCompanyNameList = new ArrayList<>();
			if(CollectionUtils.isNotEmpty(bsFunderList)){
				List<BsFunder> collect = bsFunderList.stream().filter(it -> it.getUserId().equals(vo.getUserId())).collect(Collectors.toList());
				if(CollectionUtils.isNotEmpty(collect)){
					List<String> companyList = collect.stream().map(BsFunder::getCompanyNames).collect(Collectors.toList());
					// 资金方名称可能有多个，逗号分隔的
					companyList.forEach(it->{
						ourCompanyNameList.addAll(Arrays.asList(it.split(",")));
					});
				} else {
					// 防止sql查询报错，返回空数据
					ourCompanyNameList.add("-1");
				}
			} else {
				// 防止sql查询报错，返回空数据
				ourCompanyNameList.add("-1");
			}
			vo.setOurCompanyList(ourCompanyNameList);
		}
		List<RptStockBook> list = stockInventoryMapper.findPageStockBook(vo);
		Pageable pageable = PageRequest.of(vo.getPage() - 1, vo.getRows());
		// 获取部门
		DeptSearchVo deptSearchVo = new DeptSearchVo();
		deptSearchVo.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
		List<SysDeptSdk> deptAll = authOpenFacade.findDeptAll(deptSearchVo);
		Map<Long,String > deptAllMap =new HashMap<>();
		if(CollectionUtils.isNotEmpty(deptAll)){
			deptAllMap= deptAll.stream().collect(Collectors.toMap(SysDeptSdk::getDeptId, SysDeptSdk::getDeptName));
		}
		// 处理数据，获取代采方利率,没办法通过feign 调用，
		// 启动类扫描不到basCore的，就算启动类配置了扫描
		// 有重名的feignApi 历史原因，没办法调整，重写了查询
		List<BsCompanyDcsx> dcsxCompanyList = stockInventoryMapper.findDcsxCompanyList();
		Map<String, BigDecimal> dcsxCompanyMap =new HashMap<>();
		if(CollectionUtils.isNotEmpty(dcsxCompanyList)){
			dcsxCompanyMap= dcsxCompanyList.stream().collect(Collectors.toMap(BsCompanyDcsx::getCompanyName, BsCompanyDcsx::getAnnualizedRevenue));
		}

		if(CollectionUtils.isNotEmpty(list)){
			Map<String, BigDecimal> finalDcsxCompanyMap = dcsxCompanyMap;
			Map<Long, String> finalDeptAllMap = deptAllMap;
			list.forEach(it->{
				// 设置部门名称
				String deptName = finalDeptAllMap.get(it.getDeptId());
				it.setDeptName(deptName);
				// 处理业务类型
				String businessTypeStr = "其他";
				if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_BB, it.getBusinessType()) && Boolean.TRUE.equals(it.getMatchCreditFlg())) {
					businessTypeStr = "赊销";
				} else if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_BB, it.getBusinessType()) && Boolean.FALSE.equals(it.getMatchCreditFlg())) {
					businessTypeStr = "代采";
				} else if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_XS, it.getBusinessType()) && Boolean.FALSE.equals(it.getMatchCreditFlg())) {
					businessTypeStr = "自营";
				} else if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_TP, it.getBusinessType()) && Boolean.FALSE.equals(it.getMatchCreditFlg())) {
					businessTypeStr = "托盘";
				}
				it.setBusinessTypeName(businessTypeStr);
				// 指导价毛利
				BigDecimal minSellPrice = it.getMinSellPrice();
				if(minSellPrice ==null){
					it.setMinSellPriceProfit(null);
				} else {
					BigDecimal subtract = it.getSellPrice().subtract(it.getMinSellPrice());
					BigDecimal minSellProiceProfit = subtract.multiply(it.getTotalNumber());
					it.setMinSellPriceProfit(minSellProiceProfit);
				}
				// 采购价毛利
				BigDecimal subtract = it.getSellPrice().subtract(it.getBuyPrice());
				BigDecimal buyPriceProfit = subtract.multiply(it.getTotalNumber());
				it.setBuyPriceProfit(buyPriceProfit);
				// 在库天数
				if(it.getPayFullTime()!=null&&it.getConfirmDate()!=null){

					LocalDate confirmReceiptDate = it.getConfirmDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
					LocalDate realPayFullTime = it.getPayFullTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
					// 计算两个 LocalDate 对象之间的天数差
					long daysBetween = ChronoUnit.DAYS.between(realPayFullTime, confirmReceiptDate);
					it.setWarehouseDay((int) daysBetween);
				}
				// 资金成本 计算公式=（采购价*数量）*下游我方年化利率*(下游账期+在库天数)
				BigDecimal buySum = it.getBuyPrice().multiply(it.getTotalNumber());
				Integer creditCycle = it.getCreditCycle()==null?0:it.getCreditCycle();
				Integer warehouseDay = it.getWarehouseDay()==null?0:it.getWarehouseDay();
				int creWar =0;
				// 如果仓储费为0，不需要累加在库天数，存储没费用
				if(it.getWarehouseAmount()==null||it.getWarehouseAmount().compareTo(BigDecimal.ZERO)==0){
					creWar = creditCycle;
				} else {
					creWar = creditCycle+warehouseDay;
				}
				// 获取年化利率
				BigDecimal annualizedRevenue = finalDcsxCompanyMap.get(it.getOurCompanyName());
				if(annualizedRevenue == null){
					// 默认给0.1(10%)
					annualizedRevenue = BigDecimal.valueOf(0.1);
				}
				BigDecimal dayEarnings = annualizedRevenue.divide(BigDecimal.valueOf(365), 8, RoundingMode.HALF_UP);
				BigDecimal costOfFunds = buySum.multiply(dayEarnings).multiply(BigDecimal.valueOf(creWar)).setScale(3, RoundingMode.HALF_UP);
				it.setCostOfFunds(costOfFunds);
				// 保费计算
				BigDecimal insuranceRate = it.getInsuranceRate()==null?BigDecimal.ZERO:it.getInsuranceRate();
				BigDecimal dealSumAmount = it.getSellPrice().multiply(it.getTotalNumber());
				it.setInsuranceAmount(insuranceRate.multiply(dealSumAmount).setScale(3, RoundingMode.HALF_UP));
			});
		}
		Page<RptStockBook> pageVo = new PageImpl<>(list, pageable, vo.getCount());
		return pageVo;
	}
}
