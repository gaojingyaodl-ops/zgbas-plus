package com.spt.bas.report.server.service.impl;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.spt.bas.report.client.entity.RptCtrContractOrverdur;
import com.spt.bas.report.server.dao.RptCtrContractOrverdurMapper;
import com.spt.bas.report.server.service.IRptCtrContractOrverdurService;
import com.spt.tools.core.date.DateOperator;
@Component
public class RptCtrContractOrverdurServiceImpl implements IRptCtrContractOrverdurService {
	@Autowired
	private RptCtrContractOrverdurMapper ctrContractOrverdurMapper;
	@Override
	public Page<RptCtrContractOrverdur> findPageOrverdur(RptCtrContractOrverdur vo) {
		String searchType = vo.getSearchType();
		List<RptCtrContractOrverdur> list = null;
		if(StringUtils.equals("receive", searchType)) {
			list = ctrContractOrverdurMapper.findReceivePageOrverdur(vo);
		}else {
			list = ctrContractOrverdurMapper.findPageOrverdur(vo);
			Date date = new Date();
			for (RptCtrContractOrverdur orverdur : list) {
				Date bondTime = orverdur.getPayBondTime();
				Date fullTime = orverdur.getPayFullTime();
				fullTime = DateOperator.addDays(fullTime, 1);
				//付定金时间 < 当前时间 < 付全款时间
				if(bondTime != null &&  bondTime.before(date) && fullTime.after(date)){
					//判断定金金额是否大于已收金额  a.大于  逾期金额=定金金额-已收金额  b.小于 逾期金额=0
					if(orverdur.getBondAmount().compareTo(orverdur.getDealedAmount()) > 0 ){
						orverdur.setOrverdurAmount(orverdur.getBondAmount().subtract(orverdur.getDealedAmount()));
					}else{
						orverdur.setOrverdurAmount(BigDecimal.ZERO);
					}
				}else{
					// 当前时间 > 付全款时间         逾期金额=合同总额-已收金额
					orverdur.setOrverdurAmount(orverdur.getTotalAmount().subtract(orverdur.getDealedAmount()));
				}
			}
		}
		Pageable pageable = PageRequest.of(vo.getPage() - 1, vo.getRows());
		Page<RptCtrContractOrverdur> pageVo = new PageImpl<>(list, pageable, vo.getCount());
		return pageVo;
	}
	/**
	 * 逾期收付款 合计统计
	 */
	@Override
	public RptCtrContractOrverdur findPageTotal(RptCtrContractOrverdur vo) {
		vo.setCount(-1);
		RptCtrContractOrverdur findPageTotal = null;
		String searchType = vo.getSearchType();
		if (StringUtils.equals("receive", searchType)) {
			findPageTotal = ctrContractOrverdurMapper.findReceivePageTotal(vo);
		}else {
			findPageTotal = ctrContractOrverdurMapper.findPageTotal(vo);
		}
		if(findPageTotal==null){
			findPageTotal = new RptCtrContractOrverdur();
		}
		return findPageTotal;
	}
	/**
	 * 查询所有逾期合同
	 */
	@Override
	public List<RptCtrContractOrverdur> findAllOrverdur(RptCtrContractOrverdur vo) {
		vo.setCount(-1);
		return ctrContractOrverdurMapper.findAllOrverdur(vo);
	}
}
