package com.spt.bas.report.server.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.spt.bas.report.client.entity.RptApplyInternalReport;
import com.spt.bas.report.server.dao.RptApplyInternalMapper;
import com.spt.bas.report.server.service.IRptApplyInternalService;
@Component
public class RptApplyInternalServiceImpl implements IRptApplyInternalService {
	@Autowired
	private RptApplyInternalMapper applyInternalMapper;
	@Override
	public Page<RptApplyInternalReport> findPageInternalBuy(RptApplyInternalReport vo) {
		List<RptApplyInternalReport> list = applyInternalMapper.findPageInternalBuy(vo);
		Pageable pageable = PageRequest.of(vo.getPage() - 1, vo.getRows());
		Page<RptApplyInternalReport> pageVo = new PageImpl<>(list, pageable, vo.getCount());
		return pageVo;
	}

}
