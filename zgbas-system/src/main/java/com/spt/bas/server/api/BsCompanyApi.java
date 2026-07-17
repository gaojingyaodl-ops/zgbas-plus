package com.spt.bas.server.api;

import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.entity.BsCompanyShare;
import com.spt.bas.client.vo.*;
import com.spt.bas.report.client.remote.IRptBsCompanyClient;
import com.spt.bas.server.service.IBsCompanyService;
import com.spt.bas.server.service.IDaDiAmountImportService;
import com.spt.bas.server.service.IPiccDataSyncService;
import com.spt.bas.server.service.IZhongYinAmountImportService;
import com.spt.bas.server.service.impl.BsCompanyDownloadService;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "bs/company")
public class BsCompanyApi extends BaseApi<BsCompany> {
	@Autowired
	private IBsCompanyService bsCompanyService;
	
	@Autowired
	private BsCompanyDownloadService bsCompanyDownloadService;

	@Autowired
	private IRptBsCompanyClient reportCompanyClient;

	@Autowired
	private IPiccDataSyncService piccDataSyncService;
	
	@Autowired
	private IDaDiAmountImportService daDiAmountImportService;

	@Autowired
	private IZhongYinAmountImportService zhongYinAmountImportService;

	@Override
	public IBaseService<BsCompany> getService() {
		return bsCompanyService;
	}

	@PostMapping("updateFileId")
	public void updateFileId(@RequestBody FileIdUpdateVo vo) {
		bsCompanyService.updateFileId(vo.getId(), vo.getFileId());
	}
	@PostMapping("updateZhongYinFileId")
	public void updateZhongYinFileId(@RequestBody FileIdUpdateVo vo) {
		
	}
	@PostMapping("updateScoreAndGrade")
	public void updateScoreAndGrade(@RequestBody CreditScoreUpdateVo vo) {
		bsCompanyService.updateScoreAndGrade(vo.getId(), vo.getCreditScore(),vo.getCompanyGrade());
	}
	@PostMapping("updateSupplierScoreAndGrade")
	public void updateSupplierScoreAndGrade(@RequestBody CreditScoreUpdateVo vo) {
		bsCompanyService.updateSupplierScoreAndGrade(vo.getId(), vo.getSupplierScore(),vo.getSupplierGrade());
	}

	@PostMapping("updateCompanyStatus")
	public void updateCompanyStatus(@RequestBody CompanyStatusVo vo) {
		bsCompanyService.updateCompanyStatus(vo);
	}

	@PostMapping("findByEnterpriseId")
	public List<BsCompany> findByEnterpriseId(@RequestBody Long enterpriseId) {
		return bsCompanyService.findByEnterpriseId(enterpriseId);
	}

	@PostMapping("findPageCompnay")
	public Page<BsCompany> findPageCompnay(@RequestBody BsCompanySearchVo queryVo) throws ApplicationException {
		return bsCompanyService.findPageCompnay(queryVo);
	}

	@PostMapping("findPageCompnayVo")
	public Page<BsCompanyVo> findPageCompnayVo(@RequestBody BsCompanySearchVo queryVo) throws ApplicationException {
		return bsCompanyService.findPageCompnayVo(queryVo);
	}
	@PostMapping("findPageCompnayVoExcel")
	public Page<BsCompanyVo> findPageCompnayVoExcel(@RequestBody BsCompanySearchVo queryVo) throws ApplicationException {
		return bsCompanyService.findPageCompnayVoExcel(queryVo);
	}

	@PostMapping("shareCompany")
	public BsCompanyShare shareCompany(@RequestBody BsCompanyShare vo) throws ApplicationException{
		return bsCompanyService.shareCompany(vo);
	}

	@PostMapping("queryCompanyName")
	public List<BsCompany> queryCompanyName(@RequestBody BsCompanyShare company){
		return bsCompanyService.findByCompanyName(company.getCompanyName(),company.getEnterpriseId());
	}

	@PostMapping("findCompanyAccountVo")
	public CompanyAccountVo findCompanyAccountVo(@RequestBody Long companyId){
		return bsCompanyService.findCompanyAccountVo(companyId);
	}

	@PostMapping("saveAccount")
	public BsCompany saveAccount(@RequestBody CompanyAccountVo vo) throws ApplicationException{
		return bsCompanyService.saveCompanyAccountVo(vo);
	}

	@PostMapping("updateStatusByAssigned")
	public void updateStatusByAssigned(@RequestBody CompanyStatusVo vo){
		bsCompanyService.updateStatusByAssigned(vo);
	}

	@PostMapping("getCompanyForDate")
	public List<BsCompany> getCompanyForDate(@RequestBody Long matchUserId){
		return bsCompanyService.getCompanyForDate(matchUserId);
	}

	@PostMapping("updateByIds")
	void updateByIds(@RequestBody Long[] condition){
		bsCompanyService.updateByIds(condition);
	}

	@PostMapping("refreshCompanyFlg")
	public void refreshCompanyFlg(@RequestBody Long companyId){
		bsCompanyService.refreshCompanyFlg(companyId);
	}

	@PostMapping("findByCompanyNames")
	public List<BsCompany> findByCompanyNames(@RequestBody String companyNames){
		return bsCompanyService.findByCompanyNames(companyNames);
	}


	@PostMapping("findByCompanyName")
	public BsCompany findByCompanyName(@RequestBody String companyName){
		return bsCompanyService.findByCompanyName(companyName);
	}

	@PostMapping("findByContact")
	List<BsCompany> findByContact(@RequestBody String phone){
		return bsCompanyService.findByContact(phone);
	}

	@PostMapping("findCompany")
	BsCompany findCompany(@RequestBody Long contractId){
		return bsCompanyService.findCompany(contractId);
	}

	@PostMapping(value = "getRelationShipApproveIdByCompanyId")
	public List<Long> getRelationShipApproveIdByCompanyId(@RequestBody Long matchUserId) {
		return reportCompanyClient.getRelationShipApproveIdByCompanyId(matchUserId);
	}

	@PostMapping(value = "getRelationShipApproveIdByCompanyIds")
	public List<Long> getRelationShipApproveIdByCompanyIds(@RequestBody List<Long> matchUserIds) {
		return reportCompanyClient.getRelationShipApproveIdByCompanyIds(matchUserIds);
	}

	@PostMapping(value = "updateOnLineApplyFlg")
	public void updateOnLineApplyFlg(@RequestBody BsCompany bsCompany){
		bsCompanyService.updateOnLineApplyFlg(bsCompany.getId(), bsCompany.getOnlineApplyFlg());
	}

	@PostMapping(value = "updateCreditQuote")
	void updateCreditQuote(@RequestBody CreditQuoteVo vo){
		bsCompanyService.updateCreditQuote(vo);
	}
	@PostMapping(value = "updatePiccInfo")
	public void updatePiccInfo(@RequestBody BsCompanyPiccRequestVo requestVo){
		bsCompanyService.updatePiccInfo(requestVo);
	}

	@PostMapping(value = "findCompanyCfcaSeal")
	public SignSealVo findCompanyCfcaSeal(@RequestBody Long companyId){
		return bsCompanyService.findCompanyCfcaSeal(companyId);
	}

	@PostMapping(value = "importPiccExcel")
	public List<String> importPiccExcel(@RequestBody ImportExcelVo importExcelVo){
		return piccDataSyncService.initPiccData(importExcelVo);
	}
	
	@PostMapping(value = "importDaDiExcel")
	public List<String> importDaDiExcel(@RequestBody ImportExcelVo importExcelVo){
		return daDiAmountImportService.initDaDiData(importExcelVo);
	}
	
	@PostMapping(value = "importZhongYinExcel")
	public List<String> importZhongYinExcel(@RequestBody ImportExcelVo importExcelVo){
		return zhongYinAmountImportService.initZhongYinData(importExcelVo);
	}

	/**
	 * 人保授信额度为0的修改为黑名单
	 */
	@PostMapping(value = "updateCreditRatingToBlack")
	public void updateCreditRatingToBlack(){
		bsCompanyService.updateCreditRatingToBlack();
	}
	
	/**
	 * 人保授信标识设为false额度置为0
	 */
	@PostMapping(value = "updatePiccFlgToFalse")
	public void updatePiccFlgToFalse(){
		bsCompanyService.updatePiccFlgToFalse();
	}

	@PostMapping("updatePiccApplyStatus")
	void updatePiccApplyStatus(@RequestBody CompanyStatusVo vo){
		bsCompanyService.updatePiccApplyStatus(vo);
	}

	@PostMapping("updatePiccApplyStatusAndRemark")
	void updatePiccApplyStatusAndRemark(@RequestBody CompanyStatusVo vo){
		bsCompanyService.updatePiccApplyStatusAndRemark(vo);
	}

	@PostMapping("downloadAccessReportFileZip")
	public DownLoadContractRespVo downloadAccessReportFileZip(@RequestBody BsCompanySearchVo searchVo){
		return bsCompanyDownloadService.downloadAccessReportFileZip(searchVo);
	}
	@PostMapping("exportCreditInfo0Excel")
	void exportCreditInfo0Excel(@RequestBody CompanyCreditExportVo companyCreditExportVo){
		 bsCompanyDownloadService.exportCreditInfo0Excel(companyCreditExportVo);
	}
}
