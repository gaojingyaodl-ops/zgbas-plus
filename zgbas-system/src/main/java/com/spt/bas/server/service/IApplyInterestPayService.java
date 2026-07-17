package com.spt.bas.server.service;

import com.spt.bas.client.entity.ApplyInterestPay;
import com.spt.bas.client.vo.api.RespVo;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface IApplyInterestPayService extends IBaseService<ApplyInterestPay> {
	void updateFileId(Long id, String fileId);

	RespVo<String> batchPayInterest(ApplyInterestPay entity);
}

