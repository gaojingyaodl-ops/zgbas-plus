package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.entity.BsCompanyShare;
import com.spt.bas.client.vo.*;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


@FeignClient(name = BasConstants.SERVER_NAME, path = BasConstants.SERVER_NAME + "/bs/company", url = BasConstants.SERVER_URL, configuration = FeignConfig.class)
public interface IBsCompanyClient extends BaseClient<BsCompany> {

    @PostMapping("updateFileId")
    void updateFileId(@RequestBody FileIdUpdateVo vo);

    @PostMapping("updateScoreAndGrade")
    void updateScoreAndGrade(@RequestBody CreditScoreUpdateVo vo);

    @PostMapping("updateSupplierScoreAndGrade")
    void updateSupplierScoreAndGrade(@RequestBody CreditScoreUpdateVo vo);

    @PostMapping("updateCompanyStatus")
    void updateCompanyStatus(@RequestBody CompanyStatusVo vo);

    @PostMapping("findByEnterpriseId")
    List<BsCompany> findByEnterpriseId(@RequestBody Long enterpriseId);

    @PostMapping("findPageCompnay")
    PageDown<BsCompany> findPageCompnay(@RequestBody BsCompanySearchVo queryVo);

    @PostMapping("findPageCompnayVo")
    PageDown<BsCompanyVo> findPageCompnayVo(@RequestBody BsCompanySearchVo queryVo);

    @PostMapping("findPageCompnayVoExcel")
    PageDown<BsCompanyVo> findPageCompnayVoExcel(@RequestBody BsCompanySearchVo queryVo);

    @PostMapping("shareCompany")
    BsCompanyShare shareCompany(@RequestBody BsCompanyShare vo);

    @PostMapping("queryCompanyName")
    List<BsCompany> queryCompanyName(@RequestBody BsCompanyShare companyName);

    @PostMapping("findCompanyAccountVo")
    CompanyAccountVo findCompanyAccountVo(@RequestBody Long companyId);

    @PostMapping("saveAccount")
    BsCompany saveAccount(@RequestBody CompanyAccountVo vo) throws ApplicationException;

    @PostMapping("updateStatusByAssigned")
    void updateStatusByAssigned(@RequestBody CompanyStatusVo vo);

    @PostMapping("getCompanyForDate")
    List<BsCompany> getCompanyForDate(@RequestBody Long matchUserId);

    @PostMapping("updateByIds")
    void updateByIds(@RequestBody Long[] condition);

    @PostMapping("refreshCompanyFlg")
    void refreshCompanyFlg(@RequestBody Long companyId);

    @PostMapping(value = "findByCompanyNames",consumes = "application/json;charset=UTF-8")
    List<BsCompany> findByCompanyNames(@RequestBody String companyNames);

    @PostMapping(value = "findByCompanyName",consumes = "application/json;charset=UTF-8")
    BsCompany findByCompanyName(@RequestBody String companyName);

    @PostMapping("findByContact")
    List<BsCompany> findByContact(@RequestBody String phone);

    @PostMapping("findCompany")
    BsCompany findCompany(@RequestBody Long companyId);

    @PostMapping(value = "getRelationShipApproveIdByCompanyId")
    List<Long> getRelationShipApproveIdByCompanyId(@RequestBody Long matchUserId);

    @PostMapping(value = "getRelationShipApproveIdByCompanyIds")
    List<Long> getRelationShipApproveIdByCompanyIds(@RequestBody List<Long> matchUserIds);

    @PostMapping(value = "updateOnLineApplyFlg")
    void updateOnLineApplyFlg(@RequestBody BsCompany bsCompany);

    @PostMapping(value = "updateCreditQuote")
    void updateCreditQuote(@RequestBody CreditQuoteVo vo);

    @PostMapping(value = "updatePiccInfo")
    void updatePiccInfo(@RequestBody BsCompanyPiccRequestVo requestVo);

    @PostMapping(value = "findCompanyCfcaSeal")
    SignSealVo findCompanyCfcaSeal(@RequestBody Long companyId);

    @PostMapping(value = "importPiccExcel")
    public List<String> importPiccExcel(@RequestBody ImportExcelVo importExcelVo);
    
    @PostMapping(value = "importDaDiExcel")
    public List<String> importDaDiExcel(@RequestBody ImportExcelVo importExcelVo);
    
    @PostMapping(value = "importZhongYinExcel")
    public List<String> importZhongYinExcel(@RequestBody ImportExcelVo importExcelVo);

    /**
     * 人保授信额度为0的修改为黑名单
     */
    @PostMapping(value = "updateCreditRatingToBlack")
    public void updateCreditRatingToBlack();
    
    /**
     * 人保授信标识设为false额度置为0
     */
    @PostMapping(value = "updatePiccFlgToFalse")
    public void updatePiccFlgToFalse();

    @PostMapping("updatePiccApplyStatusAndRemark")
    void updatePiccApplyStatusAndRemark(@RequestBody CompanyStatusVo vo);

    @PostMapping("downloadAccessReportFileZip")
    DownLoadContractRespVo downloadAccessReportFileZip(@RequestBody BsCompanySearchVo searchVo);
    @PostMapping("exportCreditInfo0Excel")
    void exportCreditInfo0Excel(@RequestBody CompanyCreditExportVo companyCreditExportVo);
}

