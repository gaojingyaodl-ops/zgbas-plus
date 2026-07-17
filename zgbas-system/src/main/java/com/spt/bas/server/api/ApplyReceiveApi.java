package com.spt.bas.server.api;

import com.spt.bas.client.entity.ApplyCancelDetail;
import com.spt.bas.client.entity.ApplyReceive;
import com.spt.bas.client.vo.ApplyReceiveAmountSumVo;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.server.service.IApplyReceiveService;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.service.IPmApproveService;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping(value = "apply/receive")
public class ApplyReceiveApi extends BaseApi<ApplyReceive> {
	@Autowired
	private IApplyReceiveService applyReceiveService;
	@Autowired
	private IPmApproveService pmApproveService;

	@Override
	public IBaseService<ApplyReceive> getService() {
		return applyReceiveService;
	}

	@PostMapping("updateFileId")
	public void updateFileId(@RequestBody FileIdUpdateVo vo){
		applyReceiveService.updateFileId(vo.getId(), vo.getFileId());
	}

	@PostMapping("findPageSum")
	public ApplyReceive findPageSum(@RequestBody PageSearchVo searchVo){
		return applyReceiveService.findPageSum(searchVo);
	}
	@PostMapping("findReceiveAmountSum")
	public ApplyReceiveAmountSumVo findReceiveAmountSum(@RequestParam("contractId") Long contractId){
		return applyReceiveService.findReceiveAmountSum(contractId);
	}

	@PostMapping("findReceiveAmountSumByContractNo")
	public ApplyReceiveAmountSumVo findReceiveAmountSumByContractNo(@RequestParam("contractNo") String contractNo){
		return applyReceiveService.findReceiveAmountSumByContractNo(contractNo);
	}

	@PostMapping("findPageDetail")
	public Page<ApplyCancelDetail> findPageDetail(@RequestBody PageSearchVo searchVo){
		Page<ApplyReceive> page = applyReceiveService.findPage(searchVo);
		List<ApplyCancelDetail> list = new ArrayList<ApplyCancelDetail>();
		for(ApplyReceive out:page.getContent()){
			PmApprove approve = pmApproveService.getEntity(out.getApproveId());
			ApplyCancelDetail detail = new ApplyCancelDetail();
			detail.setOldApproveId(out.getApproveId());
			if (approve != null) {
				detail.setOldApproveNo(approve.getApproveNo());
			}
			detail.setOldApproveDate(out.getCreatedDate());
			detail.setCancelAmount(out.getReceiveAmount());
			list.add(detail);
		}
		PageRequest pageRequest = PageRequest.of(searchVo.getPage()-1, searchVo.getRows());
		Page<ApplyCancelDetail> pageVo=new PageImpl<>(list, pageRequest, page.getTotalElements());
		return pageVo;
	}

	@Override
	@PostMapping("getEntity")
	public ApplyReceive getEntity(@RequestBody Long id){
		return applyReceiveService.getEntity(id);
	}

	@PostMapping("findListByContractIdAndStatus")
	List<ApplyReceive> findListByContractIdAndStatus(@RequestParam("contractId") Long contractId,@RequestParam("status") String status ){
		return applyReceiveService.findListByContractIdAndStatus(contractId, status);
	}
}

