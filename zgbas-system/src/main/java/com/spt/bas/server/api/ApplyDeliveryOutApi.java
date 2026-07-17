package com.spt.bas.server.api;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyCancelDetail;
import com.spt.bas.client.entity.ApplyDeliveryOut;
import com.spt.bas.client.entity.ApplyProductDetail;
import com.spt.bas.client.vo.ApplyDeliveryOutVo;
import com.spt.bas.client.vo.ApplyProductDetailVo;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.client.vo.MidstreamVo;
import com.spt.bas.server.service.IApplyDeliveryOutService;
import com.spt.bas.server.service.IApplyProductDetailService;
import com.spt.bas.server.util.MidstreamUtil;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.service.IPmApproveService;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping(value = "apply/deliveryOut")
public class ApplyDeliveryOutApi extends BaseApi<ApplyDeliveryOut> {
	@Autowired
	private IApplyDeliveryOutService applyDeliveryOutService;
	@Autowired
	private IApplyProductDetailService applyProductDetailService;
	@Autowired
	private IPmApproveService pmApproveService;
	@Resource
	private MidstreamUtil midstreamUtil;

	@Override
	public IBaseService<ApplyDeliveryOut> getService() {
		return applyDeliveryOutService;
	}


	@PostMapping("updateFileId")
	public void updateFileId(@RequestBody FileIdUpdateVo vo){
		applyDeliveryOutService.updateFileId(vo.getId(), vo.getFileId());
	}

	@PostMapping("findPageDetail")
	public Page<ApplyCancelDetail> findPageDetail(@RequestBody PageSearchVo searchVo){
		Page<ApplyDeliveryOut> page = applyDeliveryOutService.findPage(searchVo);
		List<ApplyCancelDetail> list = new ArrayList<ApplyCancelDetail>();
		for(ApplyDeliveryOut out:page.getContent()){
			PmApprove approve = pmApproveService.getEntity(out.getApproveId());
			ApplyCancelDetail detail = new ApplyCancelDetail();
			detail.setOldApproveId(out.getApproveId());
			if (approve != null) {
				detail.setOldApproveNo(approve.getApproveNo());
			}
			detail.setOldApproveDate(out.getCreatedDate());
			//作废总数量、总金额
			List<Object[]> numList = applyProductDetailService.sumApplyDetail(out.getId(), BasConstants.APPLY_TYPE_O);
			if(numList!=null){
				Object[] obj = numList.get(0);
				detail.setCancelNum(obj[2]==null?BigDecimal.ZERO:new BigDecimal(obj[2].toString()));
				//detail.setCancelAmount(null);
			}
			list.add(detail);
		}
		PageRequest pageRequest = PageRequest.of(searchVo.getPage()-1, searchVo.getRows());
		Page<ApplyCancelDetail> pageVo=new PageImpl<>(list, pageRequest, page.getTotalElements());
		return pageVo;
	}

    /**
     * 查询已出库未确认批次信息
     *
     * @param contractId
     *
     * @return
     */
    @PostMapping("getUnConfirmDeliveryOut")
    List<ApplyProductDetailVo> getUnConfirmDeliveryOut(@RequestBody Long contractId) {
        return applyDeliveryOutService.getUnConfirmDeliveryOut(contractId);
    }
    
    /**
     * 查询已出库批次信息
     *
     * @param contractId
     *
     * @return
     */
    @PostMapping("getAllDeliveryOut")
    List<ApplyProductDetailVo> getAllDeliveryOut(@RequestBody Long contractId) {
        return applyDeliveryOutService.getAllDeliveryOut(contractId);
    }
    
    /**
     * 查询中游已出库未确认批次信息
     *
     * @param contractId
     *
     * @return
     */
    @PostMapping("getUnConfirmDeliveryOutDcsx")
    List<ApplyProductDetailVo> getUnConfirmDeliveryOutDcsx(@RequestBody Long contractId) {
        return applyDeliveryOutService.getUnConfirmDeliveryOutDcsx(contractId);
    }

    /**
     * 查询详细
     *
     * @param applyDeliveryOutId
     *
     * @return
     */
    @PostMapping("findByApplyDeliveryOutId")
    ApplyProductDetail findByApplyDeliveryOutId(@RequestBody Long applyDeliveryOutId) {
        return applyDeliveryOutService.findByApplyDeliveryOutId(applyDeliveryOutId);
    }

    @PostMapping("findByApplyDeliveryOutApplyNo")
    ApplyProductDetail findByApplyDeliveryOutApplyNo(@RequestBody String applyNo) {
        return applyDeliveryOutService.findByApplyDeliveryOutApplyNo(applyNo);
    }


    @PostMapping("findByApplyNo")
    ApplyDeliveryOut findByApplyNo(@RequestBody String applyNo) {
        return applyDeliveryOutService.findByApplyNo(applyNo);
    }


    @PostMapping("applyDeliveryOut")
    public void applyDeliveryOut(@RequestBody ApplyDeliveryOutVo deliveryOutVo) throws ApplicationException {
        applyDeliveryOutService.applyDeliveryOut(deliveryOutVo);

    }

	/**
	 * 查询有效的出库审批单
	 *
	 * @param contractId
	 * @throws ApplicationException
	 */
	@PostMapping("findByContractIdNoStatusB")
	public List<ApplyDeliveryOut> findByContractIdNoStatusB(@RequestBody Long contractId) throws ApplicationException {
		return applyDeliveryOutService.findByContractIdNoStatusB(contractId);

	}

	@PostMapping("findEntity")
	public ApplyDeliveryOut findEntity(@RequestBody Long approveId){
		return applyDeliveryOutService.findEntity(approveId);
	}

	@PostMapping("findByContractId")
	public List<ApplyDeliveryOut> findByContractId( @RequestBody Long contractId){
		return applyDeliveryOutService.findByContractId(contractId);
	}

	@PostMapping("findByContractNo2")
	public List<ApplyDeliveryOut> findByContractNo2(@RequestBody String contractNo){
		return applyDeliveryOutService.findByContractNo2(contractNo);
	}

	@PostMapping("generateApplyNo")
	public ApplyDeliveryOut generateApplyNo(@RequestBody Long contractId){
		return applyDeliveryOutService.generateApplyNo(contractId);
	}

	@PostMapping("generateFundRate")
	public BigDecimal generateFundRate(@RequestBody MidstreamVo midstreamVo){
		return midstreamUtil.generateFundRate(midstreamVo.getCompanyName(),midstreamVo.getOurCompanyName(),midstreamVo.getContractType());
	}
}

