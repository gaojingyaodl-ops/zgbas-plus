package com.spt.bas.server.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.client.entity.ApplyCancelDetail;
import com.spt.bas.client.entity.ApplyPayRefund;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.server.service.IApplyPayRefundService;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.service.IPmApproveService;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;


@RestController
@RequestMapping(value = "apply/refundPay")
public class ApplyPayRefundApi extends BaseApi<ApplyPayRefund> {
	@Autowired
	private IApplyPayRefundService applyPayRefundService;
	@Autowired
	private IPmApproveService pmApproveService;
	
	@Override
	public IBaseService<ApplyPayRefund> getService() {
		return applyPayRefundService;
	}
	
	@PostMapping("updateFileId")
	public void updateFileId(@RequestBody FileIdUpdateVo vo) {
		applyPayRefundService.updateFileId(vo.getId(), vo.getFileId());
	}
	
	@PostMapping("findPageDetail")
	public Page<ApplyCancelDetail> findPageDetail(@RequestBody PageSearchVo searchVo){
		Page<ApplyPayRefund> page = applyPayRefundService.findPage(searchVo);
		List<ApplyCancelDetail> list = new ArrayList<ApplyCancelDetail>();
		for (ApplyPayRefund out : page.getContent()) {
			PmApprove approve = pmApproveService.getEntity(out.getApproveId());
			ApplyCancelDetail detail = new ApplyCancelDetail();
			detail.setOldApproveId(out.getApproveId());
			if (approve != null) {
				detail.setOldApproveNo(approve.getApproveNo());
			}
			detail.setOldApproveDate(out.getCreatedDate());
			detail.setCancelAmount(out.getRefundAmount());
			list.add(detail);
		}
		PageRequest pageRequest = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows());
		Page<ApplyCancelDetail> pageVo = new PageImpl<>(list, pageRequest, page.getTotalElements());
		return pageVo;
	}
	
}

