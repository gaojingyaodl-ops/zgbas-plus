package com.spt.bas.report.server.service.impl;

import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.DeptSearchVo;
import com.spt.bas.report.client.entity.RptCtrDailyBuyReport;
import com.spt.bas.report.client.entity.RptCtrDailySalesReport;
import com.spt.bas.report.client.entity.RptCtrDailyStockReport;
import com.spt.bas.report.client.entity.RptCtrMatchUserProfitReport;
import com.spt.bas.report.server.dao.RptCtrDailySalesReportMapper;
import com.spt.bas.report.server.service.IRptCtrDailySalesReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class RptCtrDailySalesReportServiceImpl implements IRptCtrDailySalesReportService {
	@Autowired
	private RptCtrDailySalesReportMapper ctrDailySalesReportMapper;
	@Autowired
	private IAuthOpenFacade authOpenFacade;

	/**
	 * 日销售明细表
	 */
	@Override
	public Page<RptCtrDailySalesReport> findDailySales(RptCtrDailySalesReport vo) {
		List<RptCtrDailySalesReport> list = ctrDailySalesReportMapper.findDailySales(vo);
		DeptSearchVo searchVo = new DeptSearchVo();
		for (RptCtrDailySalesReport report : list) {
			if(report.getSellMatchId()!=null){
				searchVo.setEnterpriseId(report.getEnterpriseId());
				searchVo.setUserId(report.getSellMatchId());
				searchVo.setDeptType("team");
				SysDeptSdk sysDept = authOpenFacade.findDept(searchVo);
				report.setTeam(sysDept==null ? null:sysDept.getDeptName());
			}
		}
		Pageable pageable = PageRequest.of(vo.getPage() - 1, vo.getRows());
		Page<RptCtrDailySalesReport> pageVo = new PageImpl<>(list, pageable, vo.getCount());
		return pageVo;
	}

	/**
	 * 日销售明细表合计
	 */
	@Override
	public RptCtrDailySalesReport findDailySalesTotal(RptCtrDailySalesReport vo) {
		vo.setCount(-1);
		RptCtrDailySalesReport total = ctrDailySalesReportMapper.findDailySalesTotal(vo);
		if(total == null){
			total = new RptCtrDailySalesReport();
		}
		return total;
	}

	/**
	 * 日采购明细表
	 */
	@Override
	public Page<RptCtrDailyBuyReport> findDailyBuy(RptCtrDailyBuyReport vo) {
		List<RptCtrDailyBuyReport> list = ctrDailySalesReportMapper.findDailyBuy(vo);
		DeptSearchVo searchVo = new DeptSearchVo();
		for (RptCtrDailyBuyReport report : list) {
			if(report.getMacthId()!=null){
				searchVo.setEnterpriseId(report.getEnterpriseId());
				searchVo.setUserId(report.getMacthId());
				searchVo.setDeptType("team");
				SysDeptSdk sysDept = authOpenFacade.findDept(searchVo);
				report.setTeam(sysDept==null ? null:sysDept.getDeptName());
			}
		}
		Pageable pageable = PageRequest.of(vo.getPage() - 1, vo.getRows());
		Page<RptCtrDailyBuyReport> pageVo = new PageImpl<>(list, pageable, vo.getCount());
		return pageVo;
	}

	/**
	 * 日采购明细合计
	 */
	@Override
	public RptCtrDailyBuyReport findDailyBuyTotal(RptCtrDailyBuyReport vo) {
		vo.setCount(-1);
		RptCtrDailyBuyReport total = ctrDailySalesReportMapper.findDailyBuyTotal(vo);
		if(total == null){
			total = new RptCtrDailyBuyReport();
		}
		return total;
	}

	/**
	 * 业务员毛利明细月报表
	 */
	@Override
	public Page<RptCtrMatchUserProfitReport> findMatchUserProfit(RptCtrMatchUserProfitReport vo) {
		List<RptCtrMatchUserProfitReport> list = ctrDailySalesReportMapper.findMatchUserProfit(vo);
		DeptSearchVo searchVo = new DeptSearchVo();
		for (RptCtrMatchUserProfitReport report : list) {
			if(report.getSellMatchId()!=null){
				searchVo.setEnterpriseId(report.getEnterpriseId());
				searchVo.setUserId(report.getSellMatchId());
				searchVo.setDeptType("team");
				SysDeptSdk sysDept = authOpenFacade.findDept(searchVo);
				report.setTeam(sysDept==null ? null:sysDept.getDeptName());
			}
		}
		Pageable pageable = PageRequest.of(vo.getPage() - 1, vo.getRows());
		Page<RptCtrMatchUserProfitReport> pageVo = new PageImpl<>(list, pageable, vo.getCount());
		return pageVo;
	}

	/**
	 * 业务员毛利明细月报表合计
	 */
	@Override
	public RptCtrMatchUserProfitReport findProfitTotal(RptCtrMatchUserProfitReport vo) {
		vo.setCount(-1);
		RptCtrMatchUserProfitReport total = ctrDailySalesReportMapper.findProfitTotal(vo);
		if(total == null){
			total = new RptCtrMatchUserProfitReport();
		}
		return total;
	}

	/**
	 * 采购合同库存日明细表
	 */
	@Override
	public Page<RptCtrDailyStockReport> findDailyStock(RptCtrDailyStockReport vo) {
		List<RptCtrDailyStockReport> list = ctrDailySalesReportMapper.findDailyStock(vo);
		DeptSearchVo searchVo = new DeptSearchVo();
		for (RptCtrDailyStockReport report : list) {
			if(report.getMatchId()!=null){
				searchVo.setEnterpriseId(report.getEnterpriseId());
				searchVo.setUserId(report.getMatchId());
				searchVo.setDeptType("team");
				SysDeptSdk sysDept = authOpenFacade.findDept(searchVo);
				report.setTeam(sysDept==null ? null:sysDept.getDeptName());
			}
		}
		Pageable pageable = PageRequest.of(vo.getPage() - 1, vo.getRows());
		Page<RptCtrDailyStockReport> pageVo = new PageImpl<>(list, pageable, vo.getCount());
		return pageVo;
	}

	/**
	 * 采购合同库存日明细统计
	 */
	@Override
	public RptCtrDailyStockReport findDailyStockTotal(RptCtrDailyStockReport vo) {
		vo.setCount(-1);
		RptCtrDailyStockReport total = ctrDailySalesReportMapper.findDailyStockTotal(vo);
		if(total == null){
			total = new RptCtrDailyStockReport();
		}
		return total;
	}

}
