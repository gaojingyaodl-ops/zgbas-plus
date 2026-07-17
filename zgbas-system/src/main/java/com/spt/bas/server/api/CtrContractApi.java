package com.spt.bas.server.api;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.dto.CtrContractDto;
import com.spt.bas.client.entity.ApplyReceive;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.vo.*;
import com.spt.bas.client.vo.protocol.DzdAgreement;
import com.spt.bas.server.ctr.service.ICtrContractInvalidService;
import com.spt.bas.server.ctr.service.ICtrContractSaveService;
import com.spt.bas.server.ctr.service.ICtrContractUpdateService;
import com.spt.bas.server.dao.CtrContractDao;
import com.spt.bas.server.service.ICtrContractService;
import com.spt.bas.server.service.impl.CtrContractDownloadService;
import com.spt.pm.entity.PmApprove;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping(value = "ctr/contract")
public class CtrContractApi extends BaseApi<CtrContract> {
	@Autowired
	private ICtrContractService ctrContractService;
	@Autowired
	private ICtrContractUpdateService ctrContractUpdateService;
	@Autowired
	private ICtrContractInvalidService ctrContractInvalidService;
	@Autowired
	private ICtrContractSaveService ctrContractSaveService;
	@Autowired
	private CtrContractDao ctrContractDao;
	@Autowired
	private CtrContractDownloadService contractDownloadService;

	@Override
	public IBaseService<CtrContract> getService() {
		return ctrContractService;
	}

	@PostMapping("updateFileId")
	public void updateFileId(@RequestBody FileIdUpdateVo vo) {
		ctrContractUpdateService.updateFileId(vo.getId(), vo.getFileId());
	}
	@PostMapping("updateDebtCertificateFileId")
	public void updateDebtCertificateFileId(@RequestBody FileIdUpdateVo vo) {
		ctrContractUpdateService.updateDebtCertificateFileId(vo.getId(), vo.getDebtCertificateFileId());
	}

	@PostMapping("updateCtrFileId")
	public void updateCtrFileId(@RequestBody FileIdUpdateVo vo) {
		ctrContractUpdateService.updateCtrFileId(vo.getId(), vo.getFileId());
	}

	@PostMapping("updateInvoiceFileId")
	public void updateInvoiceFileId(@RequestBody FileIdUpdateVo vo) {
		ctrContractUpdateService.updateInvoiceFileId(vo.getId(), vo.getFileId());
	}

	@PostMapping("updateWarehouseFileId")
	public void updateWarehouseFileId(@RequestBody FileIdUpdateVo vo) {
		ctrContractUpdateService.updateWarehouseFileId(vo.getId(), vo.getFileId());
	}

	@PostMapping("updateGoodsFileId")
	void updateGoodsFileId(@RequestBody FileIdUpdateVo vo){
		ctrContractUpdateService.updateGoodsFileId(vo.getId(),vo.getGoodsFileId());
	}

	@PostMapping("doSigning")
	public void doSigning(@RequestBody CtrContractSignRequest req) throws ApplicationException {
		ctrContractUpdateService.doSigning(req);
	}
	@PostMapping("updateDoubleCheckFileId")
	public void updateDoubleCheckFileId(@RequestBody FileIdUpdateVo vo) {
		ctrContractUpdateService.updateDoubleCheckFileId(vo.getId(), vo.getFileId());
	}

	@PostMapping("invalidTheContract")
	public void invalidTheContract(@RequestBody CtrConctractInvalidVo vo) throws ApplicationException{
		ctrContractInvalidService.invalidTheContract(vo);
	}

	@PostMapping("findApproveByOrder")
	public List<CtrContract> findApproveByOrder(@RequestBody CtrContract ctr){
		return ctrContractService.findApproveByOrder(ctr.getApproveId());
	}

	@PostMapping("findPageContract")
	public Page<ContractShowVo> findPageContract(@RequestBody ContractSearchVo queryVo){
		return ctrContractService.findPageContract(queryVo);
	}

	@PostMapping("sumPageContract")
	public CtrContract sumPageContract(@RequestBody ContractSearchVo searchVo){
		return ctrContractService.sumPageContract(searchVo);
	}

	@PostMapping("findByStockDetailId")
	public CtrContract findByStockDetailId(@RequestBody Long stockDetailId){
		return ctrContractService.findByStockDetailId(stockDetailId);
	}

	@PostMapping("findPageChoose")
	public Page<CtrContractChooseVo> findPageChoose(@RequestBody ContractSearchVo queryVo){
		return ctrContractService.findPageChoose(queryVo);
	}

	@PostMapping("findContractByLinkContractId")
	public CtrContract findContractByLinkContractId(Long linkContractId){
		return ctrContractService.findContractByLinkContractId(linkContractId);
	}

	@PostMapping("findByLinkContractIdLink")
	public List<CtrContract> findByLinkContractIdLink(@RequestBody String linkContractId){
		return ctrContractService.findByLinkContractIdLink(linkContractId);
	}

	@PostMapping("findByIdIn")
	public List<CtrContract> findByIdIn(@RequestBody String[] arr){
		Long[] lon = new Long[arr.length];
		for (int i = 0; i < arr.length; i++) {
			lon[i]=Long.valueOf(arr[i]);
		}
		return ctrContractService.findByIdIn(lon);
	}

	@PostMapping("findIdByLinkContractId")
	public List<Long> findIdByLinkContractId(@RequestBody String id){
		return ctrContractService.findIdByLinkContractId(id);
	}

	@PostMapping("findByContractId")
	public CtrContractChooseVo findByContractId(@RequestBody Long contractId){
		return ctrContractService.findByContractId(contractId);
	}

	@PostMapping("findByServiceContractId")
	public CtrServiceContractChooseVo findByServiceContractId(@RequestBody Long serviceContractId){
		return ctrContractService.findByServiceContractId(serviceContractId);
	}

	@PostMapping("findByContractNo")
	public CtrContract findByContractNo(@RequestBody CtrContract ctrContract) {
		return ctrContractService.findByContractNo(ctrContract.getContractNo());
	}

	@PostMapping("updateConfirmReceiveNumber")
	public void updateConfirmReceiveNumber(@RequestBody CtrContractOphisRequest ophisRequest) throws ApplicationException {
		ctrContractUpdateService.updateConfirmReceiveNumber(ophisRequest);
	}

	@PostMapping("findDetailByContractId")
	public CtrContractDetailVo findDetailByContractId(@RequestBody Long contractId) {
		return ctrContractService.findDetailByContractId(contractId);
	}

	@PostMapping("refreshContractText")
	public void refreshContractText(@RequestBody Long contractId) throws ApplicationException {
		if (contractId != null) {
			String contractIdStr = String.valueOf(contractId);
			ctrContractSaveService.refreshContractText(contractIdStr);
		}
	}

	@PostMapping("printApplyConfirm")
	public ApproveFormPrintVo printApplyConfirm(@RequestBody Long contractId) {
		return ctrContractService.printApplyConfirm(contractId);
	}

	@PostMapping("updateContractAmount")
	public void updateContractAmount(@RequestBody CtrContractUpdateVo updateVo) {
		ctrContractUpdateService.updateContractAmount(updateVo);
	}

	@PostMapping("getApproveHistory")
	public List<PmApproveHistoryVo> getApproveHistory(@RequestBody ContractSearchVo searchVo){
		return ctrContractService.getApproveHistory(searchVo.getBuyContractId(), searchVo.getSellContractId());
	}

	@PostMapping("findByContractNoV2")
	public CtrContract findByContractNoV2(@RequestBody String contractNo) {
		return ctrContractService.findByContractNoV2(contractNo);
	}

	/**
	 * 校验是否是背靠背合同
	 * @param contracNo
	 * @return
	 */
	@PostMapping("checkIsBkb")
	public boolean checkIsBkb(@RequestBody String contracNo){
		CtrContract contract = ctrContractService.findByContractNoV2(contracNo);
		if (contract != null && BasConstants.BUSINESS_TYPE_ZY_BB.equals(contract.getBusinessType())) {
			return true;
		}
		return false;
	}

	/**
	 * 校验是否是托盘合同
	 * @param contracNo
	 * @return
	 */
	@PostMapping("checkIsTP")
	public boolean checkIsTP(@RequestBody String contracNo){
		CtrContract contract = ctrContractService.findByContractNoV2(contracNo);
		if (contract != null && BasConstants.BUSINESS_TYPE_ZY_TP.equals(contract.getBusinessType())) {
			return true;
		}
		return false;
	}

	/**
	 * 通过一个合同id查询这个预算的上下游合同
	 * @param contractId
	 * @return
	 */
	@PostMapping("findContractsByContractId")
	public List<CtrContract> findContractsByContractId(@RequestBody Long contractId){
		CtrContract ctrContract = ctrContractService.getEntity(contractId);
		return ctrContractService.findApproveByOrder(ctrContract.getApproveId());
	}
	/**
	 * 通过approveId查询合同id
	 */
	@PostMapping("findByApproveId")
	public List<CtrContract> findByApproveId(@RequestBody Long approveId){
		return ctrContractService.findByApproveId(approveId);
	}

	@PostMapping("findAllContractByApproveId")
	public List<CtrContract> findAllContractByApproveId(@RequestBody Long approveId){
		return ctrContractDao.findAllContractByApproveId(approveId);
	}
	
	/**
	 * 通过approveId查询合同id
	 */
	@PostMapping("findByApproveIds")
	public List<CtrContract> findByApproveIds(@RequestBody List<Long> approveIds){
		return ctrContractService.findByApproveIds(approveIds);
	}

	/**
	 * 通过companyId查询合同
	 * @param companyId
	 * @return
	 */
	@PostMapping("findByCompanyId")
	public List<CtrContract> findByCompanyId(@RequestBody Long companyId){
		return ctrContractService.findByCompanyId(companyId);
	}

	/**
	 * 更新合同表中的原始金额,抵扣金额,合同实际金额
	 * @param contractNo
	 * @param actualContractAmount
	 * @param deductibleAmount
	 * @param originalContractAmount
	 */
	@GetMapping("updateContractData")
    public void updateContractData(@RequestParam("contractNo") String contractNo, @RequestParam("actualContractAmount") BigDecimal actualContractAmount, @RequestParam("deductibleAmount") BigDecimal  deductibleAmount, @RequestParam("originalContractAmount") BigDecimal originalContractAmount){
		ctrContractService.updateContractData(contractNo,actualContractAmount,deductibleAmount,originalContractAmount);
	}


	@PostMapping("updateStatusByContractNo")
	public void updateStatusByContractNo(@RequestParam("factorStatus") String factorStatus,@RequestParam("contractNo")  String contractNo){
		 ctrContractService.updateStatusByContractNo(factorStatus,contractNo);
	}

	/* 更新违约标识 */
	@PostMapping("violateFlgUpdate")
	public void violateFlgUpdate(@RequestBody Long id){
		ctrContractUpdateService.violateFlgUpdate(id);
	}

	/**
	 * 清除罚金
	 * @param id
	 */
	@PostMapping("clearPenalty")
	public void clearPenalty(@RequestBody Long id){
		ctrContractUpdateService.clearPenalty(id);
	}

	@PostMapping("refreshFactorStatus")
	public CtrContract refreshFactorStatus(@RequestBody Long contractId){
		return ctrContractUpdateService.refreshFactorStatus(contractId);
	}

	@PostMapping("findByCustomerOrderCode")
	public CtrContract findByCustomerOrderCode(@RequestParam("customerOrderCode") String customerOrderCode){
	return 	ctrContractService.findByCustomerOrderCode(customerOrderCode);
	}



	@PostMapping("updateDeliveryStaus")
	public void updateDeliveryStaus(@RequestParam("id") Long id,@RequestParam("staus")  String staus){
		ctrContractUpdateService.updateDeliveryStaus(id,staus);
	}
	/* 订单预警查询 */
	@PostMapping("findByOrderWarn")
	public Page<CtrContract> findByOrderWarn(@RequestBody ContractOrderVo queryVo){
		return ctrContractService.findByOrderWarn(queryVo);
	}
	/* 诉讼管理查询 */
	@PostMapping("findByLitigation")
	public Page<CtrContract> findByLitigation(@RequestBody ContractOrderVo queryVo){
		return ctrContractService.findByLitigation(queryVo);
	}
	/* 更新履约状态 */
	@PostMapping("updatePerformanceStatus")
	public void updatePerformanceStatus(@RequestParam("id")Long id,@RequestParam("status")String status){
		ctrContractUpdateService.updatePerformanceStatus(id,status);
	}

	@PostMapping(value = "findUnDeliveryPage")
	public Page<CtrContract> findUnDeliveryPage(@RequestBody PageSearchVo pageSearchVo){
		return ctrContractService.findUnDeliveryPage(pageSearchVo);
	}
	/* 查询公司的罚息合同 */
	@PostMapping("findByCompanyInterest")
	public Page<CtrContract> findByCompanyInterest(@RequestBody CtrContractDto ctrContractDto){
		return ctrContractService.findByCompanyInterest(ctrContractDto);
	}

	@PostMapping("judgeUseSpecialBankContractId")
	public boolean judgeUseSpecialBankContractId(@RequestBody Long contractId){
		return ctrContractService.judgeUseSpecialBankContractId(contractId);
	}

	@PostMapping("judgeUseSpecialBankApplyMatchDetailId")
	public boolean judgeUseSpecialBankApplyMatchDetailId(@RequestBody Long applyMatchDetailId){
		return ctrContractService.judgeUseSpecialBankApplyMatchDetailId(applyMatchDetailId);
	}

	@PostMapping("getDeliveryExportVo")
	public Map<Long, ApplyDeliveryExportVo> getDeliveryExportVo(@RequestBody List<Long> contractIds){
		return ctrContractService.getDeliveryExportVo(contractIds);
	}

	@PostMapping("countByCompanyId")
	public Long countByCompanyId(@RequestBody Long companyId){
		return ctrContractDao.countByCompanyId(companyId);
	}

	@PostMapping("downloadContractFileZip")
	public DownLoadContractRespVo downloadContractFileZip(@RequestBody ContractSearchVo searchVo){
		return contractDownloadService.downloadContractFileZip(searchVo);
	}
	@PostMapping("downloadContractFileMergePdf")
	DownLoadContractRespVo downloadContractFileMergePdf(@RequestBody List<CtrContractFileDownloadVo> fileDownloadVoList){
		return contractDownloadService.downloadContractFileMergePdf(fileDownloadVoList);
	}

	@PostMapping("downloadDcsxContractFileMergePdf")
	DownLoadContractRespVo downloadDcsxContractFileMergePdf(@RequestBody List<CtrContractFileDownloadVo> fileDownloadVoList){
		return contractDownloadService.downloadDcsxContractFileMergePdf(fileDownloadVoList);
	}

	@PostMapping("findByContractNoLikes")
	public List<CtrContract> findByContractNoLikes(@RequestBody String contractNo){
		return ctrContractService.findByContractNoLikes(contractNo);
	}

	@PostMapping("findDiscountContractList")
	public List<ApplyReceive> findDiscountContractList(@RequestBody CtrContract ctrContract){
		return ctrContractService.findDiscountContractList(ctrContract);
	}

	@PostMapping("findTpDiscountContractList")
	public List<ApplyReceive> findTpDiscountContractList(@RequestBody CtrContract ctrContract){
		return ctrContractService.findTpDiscountContractList(ctrContract);
	}

	@PostMapping("findContractByIds")
	public List<CtrContract> findContractByIds(@RequestBody Long[] arr){
		return ctrContractService.findByIdIn(arr);
	}

	@PostMapping("findOverdueContractListByCompanyId")
	public List<CtrContract> findOverdueContractListByCompanyId(@RequestBody Long companyId){
		return ctrContractService.findOverdueContractListByCompanyId(companyId);
	}
	
	@PostMapping("findUnDelivery3Day")
	public Boolean findUnDelivery3Day(@RequestBody Long companyId){
		return ctrContractService.findUnDelivery3Day(companyId);
	}

	@PostMapping("findSpecialChainContract")
	public CtrContract findSpecialChainContract(@RequestBody Long approveId){
		return ctrContractService.findSpecialChainContract(approveId);
	}

	@PostMapping("sumByLitigation")
	public CtrContract sumByLitigation(@RequestBody ContractOrderVo queryVo){
		return ctrContractService.sumByLitigation(queryVo);
	}

	@PostMapping("filterAutoSignWithPay")
	public List<PmApprove> filterAutoSignWithPay(@RequestBody List<PmApprove> autoSignApproveList){
		return ctrContractService.filterAutoSignWithPay(autoSignApproveList);
	}

	@PostMapping("getDzdAgreement")
	DzdAgreement getDzdAgreement(@RequestBody ProtocolDocumentSearchVo searchVo) {
		return ctrContractService.getDzdAgreement(searchVo);
	}
}

